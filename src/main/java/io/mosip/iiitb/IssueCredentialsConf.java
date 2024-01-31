package io.mosip.iiitb;

import lombok.Data;

@Data
public class IssueCredentialsConf {
    String appId;
    String clientId;
    String clientPass;
    String issuer;
}
