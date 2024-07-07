package io.mosip.iiitb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

        @JsonProperty("transaction_limit")
        private Integer transactionLimit;

        @JsonProperty("TOKEN")
        private String tokenId;

        @JsonProperty("id_hash")
        private String idHash;
}
