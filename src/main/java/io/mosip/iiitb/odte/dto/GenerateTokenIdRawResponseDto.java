package io.mosip.iiitb.odte.dto;

import io.mosip.iiitb.odte.lib.MosipResponseError;
import lombok.Data;

@Data
public class GenerateTokenIdRawResponseDto {
    GenerateTokenIdResponseDto response;
    MosipResponseError[] errors;
}