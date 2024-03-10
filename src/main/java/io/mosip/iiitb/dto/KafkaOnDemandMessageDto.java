package io.mosip.iiitb.dto;

import io.mosip.iiitb.lib.MosipResponseError;
import lombok.Data;

import java.util.Date;

/*
 ** TODO: fill missing fields
 **  Many of the fields are skipped in this compared to the original object
 */
@Data
public class KafkaOnDemandMessageDto {
    String publisher;
    String topic;
    Date publishedOn;
    KafkaOnDemandMessageEventDto event;

    @Data
    public static class KafkaOnDemandMessageEventDto {
        String id;
        Date timestamp;
        KafkaOnDemandMessageEventDataDto data;
    }

    @Data
    public static class KafkaOnDemandMessageEventDataDto {
        String error_message;
        String error_Code;
        String entityName;
        String requestdatetime;
        String authPartnerId;
        String individualId;
        String individualIdType;
        String requestSignature;
    }

}
