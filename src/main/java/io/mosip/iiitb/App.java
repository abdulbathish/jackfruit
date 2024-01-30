package io.mosip.iiitb;

import io.mosip.iiitb.lib.MessageBrokerWrapper;
import io.mosip.iiitb.lib.MessageBrokerWrapperConfigArg;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        String
                groupId = "my-group",
                brokers = "localhost:9092",
                topic = "quickstart-events";

        MessageBrokerWrapperConfigArg mbConfig = new MessageBrokerWrapperConfigArg();
        mbConfig.setTopic(topic);
        mbConfig.setBrokers(brokers);
        mbConfig.setGroupId(groupId);
        MessageBrokerWrapper consumer = new MessageBrokerWrapper(mbConfig);
        consumer.start();
    }
}