package io.mosip.iiitb.lib;

import lombok.Data;

@Data
public class CredentialRequestGeneratorRawResponseDto {
    CredentialRequestGeneratorResponseDto response;
    MosipResponseError[] errors;
}
