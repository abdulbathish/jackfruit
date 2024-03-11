package io.mosip.iiitb.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.consumers.DecryptedOnDemandTemplateRecord;
import io.mosip.iiitb.consumers.EventConsumerResponse;
import io.mosip.iiitb.consumers.OnDemandTemplateExtractionConsumerImpl;
import io.mosip.iiitb.dto.KafkaOnDemandMessageDto;
import io.mosip.iiitb.utils.RSACryptoTool;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;


public class MessageBrokerWrapper {
    private final OnDemandTemplateExtractionConsumerImpl odteConsumer;
    private final KafkaConsumer<String, String> consumer;
    private final RSACryptoTool rsaCryptoTool;

    @Inject
    public MessageBrokerWrapper(
            OnDemandAppConfig mbConfig,
            OnDemandTemplateExtractionConsumerImpl odteConsumer,
            RSACryptoTool rsaCryptoTool
    ) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String topic = mbConfig.kafkaTopic();

        this.consumer = initializeKafka(
                mbConfig
        );
        DummyConsumerRebalanceListener rebalancedListener = new DummyConsumerRebalanceListener();
        this.consumer.subscribe(Collections.singletonList(topic), rebalancedListener);
        this.odteConsumer = odteConsumer;
        this.rsaCryptoTool = rsaCryptoTool;
    }
    public void start() {
        System.out.println("Starting consumer");
        try {
            loopIt();
        } finally {
            this.consumer.close();
        }
    }

    private KafkaConsumer<String, String> initializeKafka(
            OnDemandAppConfig config
    ) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafkaBrokers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.kafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName()
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName()
        );

        System.out.printf(
                "Listening on %s:%s\nTopics = %s\nGroupId = %s\nBrokers = %s\n",
                config.kafkaHostname(),
                config.kafkaPort(),
                config.kafkaTopic(),
                config.kafkaGroupId(),
                config.kafkaBrokers()
        );
        return new KafkaConsumer<>(props);
    }

    private void loopIt() {
        while (true) {
            final ConsumerRecords<String, String> consumerRecords =
                    this.consumer.poll(Duration.ofMillis(100));
            if (consumerRecords.count() == 0) {
                continue;
            }

            for (ConsumerRecord<String, String> record : consumerRecords) {
                // byte[] value = EventDataDecryption.getDecryptedRecord(record);

                DecryptedOnDemandTemplateRecord drc = parseOnDemandTemplateRecord(record);
                if (drc != null) {
                    EventConsumerResponse evtConsumerResponse = this.odteConsumer.processRecord(drc);
                }
            }
            this.consumer.commitAsync();
        }
    }

    private DecryptedOnDemandTemplateRecord parseOnDemandTemplateRecord(ConsumerRecord<String, String> record) {
        String jsonMessage = record.value();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        KafkaOnDemandMessageDto parsedRecord = null;
        try {
            parsedRecord = objectMapper.readValue(jsonMessage, KafkaOnDemandMessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String encryptedIdBase64 = parsedRecord.getEvent().getData().getIndividualId();

        try {
            byte[] encryptedId = Base64.getDecoder().decode(encryptedIdBase64);
            byte[] idBytes = this.rsaCryptoTool.decryptData(encryptedId);
            String id = new String(idBytes);
            DecryptedOnDemandTemplateRecord dtr = new DecryptedOnDemandTemplateRecord();
            dtr.setId(id);
            return dtr;
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Skipping processing of record as it failed to parse");
            return null;
        }
    }

    private static class DummyConsumerRebalanceListener implements ConsumerRebalanceListener {
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> collection) {
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> collection) {
        }
    }

}
