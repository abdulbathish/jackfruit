package io.mosip.iiitb;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mosip.iiitb.dto.CredentialRequestAdditionalDataDto;
import io.mosip.iiitb.entity.UinHashSaltEntity;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.dto.IssueCredentialsResponseDto;
import io.mosip.iiitb.repository.UinHashSaltRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class App {
    public static void main(String[] args) throws JsonProcessingException, NoSuchAlgorithmException {
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


        String authToken = null;
        try {
            authToken = apiRequestService.getAuthToken(appId, clientId, clientPass);
            System.out.printf("authToken = %s\n", authToken);

        } catch (IOException | InterruptedException ex) {
            System.err.println("Failed to get auth token");
            System.err.println(ex);
            System.exit(1);
        }

        String tokenId = null;
        try {
            tokenId = apiRequestService.generateTokenId(vid, partnerCode, authToken);
            System.out.printf("tokenId = %s\n", tokenId);
        } catch (Exception ex) {
            System.err.println("Failed to get  token id");
            System.err.println(ex);
            System.exit(2);
        }
        CredentialRequestAdditionalDataDto credentialRequestAdditionalData = getAdditionalData(
                vid,
                "VID",
                tokenId
        );

        System.out.printf("additional data = %s\n", credentialRequestAdditionalData);

        String requestId = null;
        try {
            requestId = apiRequestService.getCredentialRequestId(
                    authToken,
                    vid,
                    issueCredentialsAuthConf.issuer,
                    credentialRequestAdditionalData
            );
            System.out.printf("request id = %s\n", requestId);
        } catch (IOException | InterruptedException ex) {
            System.err.println("Failed to get requestId");
            System.err.println(ex);
            System.exit(3);
        }

        String issueCredAuthToken = null;
        try {
            issueCredAuthToken = apiRequestService.getAuthToken(
                    issueCredentialsAuthConf.appId,
                    issueCredentialsAuthConf.clientId,
                    issueCredentialsAuthConf.clientPass
            );
        }  catch (IOException | InterruptedException ex) {
            System.err.println("Failed to get issueCredAuthToken ");
            System.err.println(ex);
            System.exit(4);
        }

        String issueStatus = null;
        try {

            IssueCredentialsResponseDto response = apiRequestService.issueCredentials(
                issueCredAuthToken,
                vid,
                partnerCode,
                requestId,
                credentialRequestAdditionalData
            );
            if (response != null) {
                String status = response.getStatus();
                issueStatus = status;
            }
        } catch (Exception e) {
            System.err.println("Error = " + e);
            System.exit(44);
        }
        System.out.println("Status = " + issueStatus);

//        MessageBrokerWrapperConfigArg mbConfig = new MessageBrokerWrapperConfigArg();
//        mbConfig.setTopic(topic);
//        mbConfig.setBrokers(brokers);
//        mbConfig.setGroupId(groupId);
//        MessageBrokerWrapper consumer = new MessageBrokerWrapper(mbConfig);
//        consumer.start();
    }

    private static CredentialRequestAdditionalDataDto getAdditionalData(
            final String id,
            final String idType,
            final String tokenId
    ) throws JsonProcessingException {
        int idRepoModulo = 1000;
        String salt = getSaltForVid(id);
        String idHash = null;
        try {
            idHash = generateIdHash(id, salt);
        } catch (NoSuchAlgorithmException ex) {
            System.out.printf("Warn: %s", ex);
        }
        CredentialRequestAdditionalDataDto additionalData = new CredentialRequestAdditionalDataDto();
        additionalData.setIdType(idType);
        additionalData.setTokenId(tokenId);
        additionalData.setModulo(Integer.toString(idRepoModulo));
        additionalData.setSalt(salt);
        additionalData.setExpiryTimestamp("9999-12-31T23:59:59.999Z");
        additionalData.setIdHash(idHash);
       return additionalData;
    }

    public static String generateIdHash(
            final String id,
            final String salt
    ) throws NoSuchAlgorithmException {
        String message = String.format("%s%s", id, salt);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] utf8Message = message.getBytes(StandardCharsets.UTF_8);
        byte[] digest = sha256.digest(utf8Message);
        String idHash = bytesToHex(digest);
        return idHash;
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(final byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = (byte) HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = (byte) HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static String getSaltForVid(String vid) {
        BigInteger idRepoModulo = new BigInteger("1000");
        BigInteger id = new BigInteger(vid);
        int modulo = id.mod(idRepoModulo).intValue();
        String salt = getSaltFromDB(modulo);
        return salt;
    }

    public static String getSaltFromDB(int id) {
        UinHashSaltRepository uhsr = new UinHashSaltRepository();
        UinHashSaltEntity saltEntity = uhsr.findById(id);
        if (saltEntity == null)
            System.out.println("salt entity not found");
        else
            System.out.printf("salt = %s", saltEntity.getSalt());

        return saltEntity != null ? saltEntity.getSalt() : null;
    }
}