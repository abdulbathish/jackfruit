package io.mosip.iiitb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
public class CredentialRequestAdditionalDataDto {

        @JsonProperty("SALT")
        private String salt;

        @JsonProperty("expiry_timestamp")
        private String expiryTimestamp;

        @JsonProperty("idType")
        private String idType;
        @JsonProperty("MODULO")
        private String modulo;

        @JsonInclude(JsonInclude.Include.ALWAYS)
        @JsonProperty("transaction_limit")
        private Integer transactionLimit;

        @JsonProperty("TOKEN")
        private String tokenId;

        @JsonProperty("id_hash")
        private String idHash;
}
