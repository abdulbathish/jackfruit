package io.mosip.iiitb.consumers;

import io.mosip.iiitb.consumers.EventConsumer;
import java.lang.Exception;

public class OnDemandTemplateExtractionConsumerImpl  implements EventConsumer {


    @Override
    public <T extends DecryptedRecord> EventConsumerResponse processRecord(T record) {
        String id = record.getValue();

        System.out.println("id = " + id);
        EventConsumerResponse ecr = new EventConsumerResponse();
        ecr.setStatus(EventConsumerStatus.SUCCESS);
        return ecr;
    }
}
