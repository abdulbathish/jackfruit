package io.mosip.iiitb;

import io.mosip.iiitb.entity.UinHashSaltEntity;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.dto.IssueCredentialsResponseDto;
import io.mosip.iiitb.repository.UinHashSaltRepository;

public class App {
    public static void main(String[] args) {

        testDb();
        System.exit(0);
        String
                groupId = "my-group",
                brokers = "localhost:9092",
                topic = "quickstart-events";

        String appId ="regproc";
        String clientId="mosip-regproc-client";
        String clientPass="abc123";
        String vid = "2173918729419564";
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
            System.exit(44);
        }

//        MessageBrokerWrapperConfigArg mbConfig = new MessageBrokerWrapperConfigArg();
//        mbConfig.setTopic(topic);
//        mbConfig.setBrokers(brokers);
//        mbConfig.setGroupId(groupId);
//        MessageBrokerWrapper consumer = new MessageBrokerWrapper(mbConfig);
//        consumer.start();
    }

    public static void testDb() {
        UinHashSaltRepository uhsr = new UinHashSaltRepository();

        UinHashSaltEntity saltEntity = uhsr.findById(1);
        if (saltEntity == null)
            System.out.println("salt entity not found");
        else
            System.out.printf("salt = %s", saltEntity.getSalt());

        return;
    }
}