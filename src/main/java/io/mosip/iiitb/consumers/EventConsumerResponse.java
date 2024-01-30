package io.mosip.iiitb.consumers;

import io.mosip.iiitb.consumers.EventConsumerStatus;
import lombok.Data;

@Data
public class EventConsumerResponse {
    public EventConsumerStatus status;
    public String message;
}
