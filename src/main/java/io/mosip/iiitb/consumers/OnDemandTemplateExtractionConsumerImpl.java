package io.mosip.iiitb.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import io.mosip.iiitb.IssueCredentialsConf;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.dto.CredentialRequestAdditionalDataDto;
import io.mosip.iiitb.dto.IssueCredentialsRawResponseDto;
import io.mosip.iiitb.dto.IssueCredentialsResponseDto;
import io.mosip.iiitb.lib.ApiRequestService;
import io.mosip.iiitb.lib.MosipResponseError;
import io.mosip.iiitb.utils.SaltUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static io.mosip.iiitb.utils.Utilities.generateIdHash;

public class OnDemandTemplateExtractionConsumerImpl  implements EventConsumer<DecryptedOnDemandTemplateRecord> {

    private final ApiRequestService apiRequestService;
    private final SaltUtil saltUtil;
    private final OnDemandAppConfig config;

    private final Logger logger;

    @Inject
    public OnDemandTemplateExtractionConsumerImpl(
            ApiRequestService apiRequestService,
            SaltUtil saltUtil,
            OnDemandAppConfig config,
            Logger logger
    ) {
        this.logger = logger;
        this.apiRequestService = apiRequestService;
        this.saltUtil = saltUtil;
        this.config = config;
    }

    @Override
    public EventConsumerResponse processRecord(DecryptedOnDemandTemplateRecord record) {
        String id = record.getId();
        String vid = id;
        IssueCredentialsConf issueCredentialsAuthConf = new IssueCredentialsConf();
        issueCredentialsAuthConf.setAppId(config.issueCredsAppId());
        issueCredentialsAuthConf.setClientId(config.issueCredsClientId());
        issueCredentialsAuthConf.setClientPass(config.issueCredsClientPass());
        issueCredentialsAuthConf.setIssuer(config.issueCredsPartnerCode());

        this.logger.debug(
                String.format(
                "issue CredentialsAuthConf = %s",
                        issueCredentialsAuthConf
                    )
        );
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
            logger.debug(
                String.format(
                    "authToken = %s\n",
                    authToken
                )
            );
        } catch (IOException | InterruptedException ex) {
            logger.error("Failed to get auth token");
            logger.error(ex.getMessage());
            System.exit(1);
        }

        String tokenId = null;
        try {
            tokenId = apiRequestService.generateTokenId(vid, partnerCode, authToken);
            logger.debug(
                    String.format(
                            "tokenId = %s\n",
                            tokenId
                    )
            );
        } catch (Exception ex) {
            logger.error("Failed to get  token id");
            logger.error(ex.getMessage());
            System.exit(2);
        }

        CredentialRequestAdditionalDataDto credentialRequestAdditionalData;

        try {
            credentialRequestAdditionalData = this.getAdditionalData(
                    vid,
                    "VID",
                    tokenId
            );
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage());
            EventConsumerResponse ecr = new EventConsumerResponse();
            ecr.setStatus(EventConsumerStatus.ERROR);
            return ecr;
        }

        logger.debug("additional data = {}}\n", credentialRequestAdditionalData);

        String requestId = null;
        try {
            requestId = apiRequestService.getCredentialRequestId(
                    authToken,
                    vid,
                    issueCredentialsAuthConf.getIssuer(),
                    credentialRequestAdditionalData
            );
            logger.debug("request id = {}\n", requestId);
        } catch (IOException | InterruptedException ex) {
            logger.error("Failed to get requestId");
            logger.error(ex.getMessage());
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
            logger.error("Failed to get issueCredAuthToken ");
            logger.error(ex.getMessage());
            System.exit(4);
        }

        String issueStatus = null;
        try {
            IssueCredentialsRawResponseDto rawResponse = apiRequestService.issueCredentials(
                    issueCredAuthToken,
                    vid,
                    partnerCode,
                    requestId,
                    credentialRequestAdditionalData
            );
            this.logger.debug(
                String.format("Issue credential response = %s", rawResponse)
            );
            MosipResponseError[] errors = rawResponse.getErrors();
            if (errors != null && errors.length > 0) {
                logger.error("Failed To Issue The given credentials.");
                for (MosipResponseError error: errors) {
                    logger.error(
                            String.format("%s", error)
                    );
                }
            }
            IssueCredentialsResponseDto response = rawResponse.getResponse();
            if (response != null) {
                issueStatus = response.getStatus();
            }
        } catch (Exception e) {
            logger.error("Error = " + e);
            System.exit(44);
        }
        logger.debug("Status = " + issueStatus);

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
            logger.debug("Warn: %s", ex);
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