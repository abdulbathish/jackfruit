package io.mosip.iiitb.odte.consumers;

abstract public interface EventConsumer<T> {
    public EventConsumerResponse processRecord(T record);
    // public EventConsumerResponse processRecord(String record);
}