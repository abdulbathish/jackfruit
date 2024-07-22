package io.mosip.iiitb.odte.lib;

import lombok.Data;


@Data
public class MosipResponseError {
    private String errorCode;
    private String message;
}
