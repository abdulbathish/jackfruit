package io.mosip.iiitb.lib;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public  class IssueCredentialsResponseDto {
    private String  status;
    private String  credentialId;
    private LocalDateTime issuanceDate;
    private String  signature;
    private String  dataShareUrl;

}
