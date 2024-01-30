package io.mosip.iiitb.consumers;

import lombok.Data;

abstract public interface EventConsumer {
    public <T extends  DecryptedRecord> EventConsumerResponse processRecord(T record);
    // public EventConsumerResponse processRecord(String record);
}


