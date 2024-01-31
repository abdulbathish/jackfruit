package io.mosip.iiitb.lib;

import lombok.Data;

@Data
public class GenerateTokenIdRawResponseDto {
    GenerateTokenIdResponseDto response;
    MosipResponseError[] errors;
}