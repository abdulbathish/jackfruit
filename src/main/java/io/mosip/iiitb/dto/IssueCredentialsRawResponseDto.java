package io.mosip.iiitb.dto;

import io.mosip.iiitb.lib.MosipResponseError;
import lombok.Data;


@Data
public class IssueCredentialsRawResponseDto {
    IssueCredentialsResponseDto response;
    MosipResponseError[] errors;
}
