package io.mosip.iiitb.consumers;

import com.google.gson.JsonElement;
import io.mosip.iiitb.consumers.EventConsumer;
import io.mosip.iiitb.lib.ApiRequestService;

import java.lang.Exception;

public class OnDemandTemplateExtractionConsumerImpl  implements EventConsumer {

    private ApiRequestService apiRequestService;

    @Override
    public <T extends DecryptedRecord> EventConsumerResponse processRecord(T record) {
        String id = record.getValue();

        System.out.println("id = " + id);

        String appId ="regproc";
        String clientId="mosip-regproc-client";
        String clientPass="abc123";
        ApiRequestService apiRequestService = new ApiRequestService();
        try {
            String authToken = apiRequestService.getAuthToken(appId, clientId, clientPass);

        } catch (Exception e) {
            System.err.println("something went wrong" + e.getMessage());
        }


        EventConsumerResponse ecr = new EventConsumerResponse();
        ecr.setStatus(EventConsumerStatus.SUCCESS);
        return ecr;
    }
}
