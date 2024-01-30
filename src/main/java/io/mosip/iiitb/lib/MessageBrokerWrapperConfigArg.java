package io.mosip.iiitb.lib;

import lombok.Data;

@Data
public class MessageBrokerWrapperConfigArg {
    private String groupId;
    private String brokers;
    private String topic;
}