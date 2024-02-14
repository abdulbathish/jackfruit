package io.mosip.iiitb.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonElement;
import com.google.inject.Inject;
import io.mosip.iiitb.IssueCredentialsConf;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.consumers.EventConsumer;
import io.mosip.iiitb.dto.CredentialRequestAdditionalDataDto;
import io.mosip.iiitb.dto.IssueCredentialsResponseDto;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.utils.SaltUtil;

import java.io.IOException;
import java.lang.Exception;
import java.security.NoSuchAlgorithmException;

import static io.mosip.iiitb.utils.Utilities.generateIdHash;

public class OnDemandTemplateExtractionConsumerImpl  implements EventConsumer {

    private final ApiRequestService apiRequestService;
    private final SaltUtil saltUtil;
    private final OnDemandAppConfig config;

    @Inject
    public OnDemandTemplateExtractionConsumerImpl(
            ApiRequestService apiRequestService,
            SaltUtil saltUtil,
            OnDemandAppConfig config
    ) {
        this.apiRequestService = apiRequestService;
        this.saltUtil = saltUtil;
        this.config = config;
    }


    @Override
    public <T extends DecryptedRecord> EventConsumerResponse processRecord(T record) {
        String id = record.getValue();
        String vid = id;
        IssueCredentialsConf issueCredentialsAuthConf = new IssueCredentialsConf();
        issueCredentialsAuthConf.setAppId(config.issueCredsAppId());
        issueCredentialsAuthConf.setClientId(config.issueCredsClientId());
        issueCredentialsAuthConf.setClientPass(config.issueCredsClientPass());
        issueCredentialsAuthConf.setIssuer(config.issueCredsPartnerCode());

        String appId = config.regprocAppId();
        String clientId = config.regprocClientId();
        String clientPass = config.regprocClientPass();
        String partnerCode = config.regprocPartnerCode();
        String authToken = null;
        try {
            authToken = apiRequestService.getAuthToken(
                appId,
                clientId,
                clientPass
            );
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

        CredentialRequestAdditionalDataDto credentialRequestAdditionalData = null;

        try {
            credentialRequestAdditionalData = this.getAdditionalData(
                    vid,
                    "VID",
                    tokenId
            );
        } catch (JsonProcessingException ex) {
            System.err.println(ex);
            EventConsumerResponse ecr = new EventConsumerResponse();
            ecr.setStatus(EventConsumerStatus.ERROR);
            return ecr;
        }

        System.out.printf("additional data = %s\n", credentialRequestAdditionalData);

        String requestId = null;
        try {
            requestId = apiRequestService.getCredentialRequestId(
                    authToken,
                    vid,
                    issueCredentialsAuthConf.getIssuer(),
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
                    issueCredentialsAuthConf.getAppId(),
                    issueCredentialsAuthConf.getClientId(),
                    issueCredentialsAuthConf.getClientPass()
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

        EventConsumerResponse ecr = new EventConsumerResponse();
        ecr.setStatus(EventConsumerStatus.SUCCESS);
        return ecr;
    }

    private CredentialRequestAdditionalDataDto getAdditionalData(
            final String id,
            final String idType,
            final String tokenId
    ) throws JsonProcessingException {
        int idRepoModulo = this.config.saltRepoModulo();
        String salt = this.saltUtil.getSaltForVid(id);
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
}
