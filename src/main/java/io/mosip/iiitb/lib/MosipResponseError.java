package io.mosip.iiitb.lib;

import lombok.Data;


@Data
public class MosipResponseError {
    private String errorCode;
    private String message;


    public MosipResponseError() {
        // No-argument constructor
    }

    public MosipResponseError(
            String errorCode,
            String message
    ) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
