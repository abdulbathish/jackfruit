package io.mosip.iiitb.lib;

import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.consumers.DecryptedOnDemandTemplateRecord;
import io.mosip.iiitb.consumers.EventConsumerResponse;
import io.mosip.iiitb.consumers.OnDemandTemplateExtractionConsumerImpl;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;


import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;


public class MessageBrokerWrapper {
    private final OnDemandTemplateExtractionConsumerImpl odteConsumer;
    private final KafkaConsumer<String, String> consumer;

    @Inject
    public MessageBrokerWrapper(
            OnDemandAppConfig mbConfig,
            OnDemandTemplateExtractionConsumerImpl odteConsumer
    ) {
        String topic = mbConfig.kafkaTopic();

        this.consumer = initializeKafka(
                mbConfig
        );
        DummyConsumerRebalanceListener rebalancedListener = new DummyConsumerRebalanceListener();
        this.consumer.subscribe(Collections.singletonList(topic), rebalancedListener);
        this.odteConsumer = odteConsumer;
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
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer"
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer"
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
                DecryptedOnDemandTemplateRecord drc = new DecryptedOnDemandTemplateRecord();
                drc.setValue(record.value());
                EventConsumerResponse evtConsumerResponse = this.odteConsumer.processRecord(drc);
            }
            this.consumer.commitAsync();
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
