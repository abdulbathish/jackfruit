package io.mosip.iiitb.odte.consumers;

import lombok.Data;

@Data
public class EventConsumerResponse {
    public EventConsumerStatus status;
    public String message;
}
