package io.mosip.iiitb.consumers.stages;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;



public class EventDataDecryption {
    public static <DecryptedRecord> DecryptedRecord getDecryptedRecord(
        ConsumerRecord<byte[], byte[]> record
    ) {
        byte[] recordValue = record.value();
        return (DecryptedRecord)  recordValue;
    }
}
