package io.mosip.iiitb.lib;

import lombok.Data;

@Data
public class GetAuthTokenResponseDto {
    MosipResponseError[] errors;
}
