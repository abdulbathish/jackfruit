package io.mosip.iiitb.lib;

import lombok.Data;
import lombok.Getter;

@Data
public class IssueCredentialsRawResponseDto {
    IssueCredentialsResponseDto response;
    MosipResponseError[] errors;
}
