package io.mosip.iiitb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.lib.IssueCredentialsRawResponseDto;
import io.mosip.iiitb.lib.IssueCredentialsResponseDto;


public class App {
    public static void main(String[] args) throws Exception {
        String
                groupId = "my-group",
                brokers = "localhost:9092",
                topic = "quickstart-events";

        String appId ="regproc";
        String clientId="mosip-regproc-client";
        String clientPass="abc123";
        String vid = "54275810720514301";
        String partnerCode = "mpartner-default-auth";
        ApiRequestService apiRequestService = new ApiRequestService();

        IssueCredentialsConf issueCredentialsAuthConf = new IssueCredentialsConf();
        issueCredentialsAuthConf.setAppId("crereq");
        issueCredentialsAuthConf.setClientId("mosip-crereq-client");
        issueCredentialsAuthConf.setClientPass("abc123");
        issueCredentialsAuthConf.setIssuer(partnerCode);

        try {
            String authToken = apiRequestService.getAuthToken(appId, clientId, clientPass);
            System.out.printf("authToken = %s\n", authToken);
            String tokenId = apiRequestService.generateTokenId(vid, partnerCode, authToken);
            System.out.printf("tokenId = %s\n", tokenId);
            String requestId = apiRequestService.getCredentialRequestId(
                    authToken,
                    tokenId,
                    vid,
                    "VID",
                    issueCredentialsAuthConf.issuer
            );
            System.out.printf("request id = %s\n", requestId);
            String issueCredentialsAuthToken = apiRequestService.getAuthToken(
                    issueCredentialsAuthConf.appId,
                    issueCredentialsAuthConf.clientId,
                    issueCredentialsAuthConf.clientPass
            );
            IssueCredentialsResponseDto response = apiRequestService.issueCredentials(
                    issueCredentialsAuthToken,
                    vid,
                    "VID",
                    partnerCode,
                    requestId
            );
            if (response != null) {
                String status = response.getStatus();
                System.out.println("Status = " + status);
            }

        } catch (Exception e) {
            System.err.println("Error = " + e);
        }

//        MessageBrokerWrapperConfigArg mbConfig = new MessageBrokerWrapperConfigArg();
//        mbConfig.setTopic(topic);
//        mbConfig.setBrokers(brokers);
//        mbConfig.setGroupId(groupId);
//        MessageBrokerWrapper consumer = new MessageBrokerWrapper(mbConfig);
//        consumer.start();
    }
}