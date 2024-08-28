package io.mosip.iiitb.odte.consumers;

//import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import io.mosip.iiitb.odte.IssueCredentialsConf;
import io.mosip.iiitb.odte.config.OnDemandAppConfig;
import io.mosip.iiitb.odte.dto.CredentialRequestAdditionalDataDto;
import io.mosip.iiitb.odte.dto.IssueCredentialsRawResponseDto;
import io.mosip.iiitb.odte.dto.IssueCredentialsResponseDto;
import io.mosip.iiitb.odte.lib.ApiRequestService;
import io.mosip.iiitb.odte.lib.MosipResponseError;
import io.mosip.iiitb.odte.utils.SaltUtil;
import lombok.Data;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static io.mosip.iiitb.odte.utils.Utilities.generateIdHash;

public class OnDemandTemplateExtractionConsumerImpl  implements EventConsumer<DecryptedOnDemandTemplateRecord> {

    private final ApiRequestService apiRequestService;
    private final SaltUtil saltUtil;
    private final OnDemandAppConfig config;

    private final Logger logger;
    private final IssueCredentialsConf issueCredentialsConf;

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

        IssueCredentialsConf issueCredentialsAuthConf = new IssueCredentialsConf();
        issueCredentialsAuthConf.setAppId(config.issueCredsAppId());
        issueCredentialsAuthConf.setClientId(config.issueCredsClientId());
        issueCredentialsAuthConf.setClientPass(config.issueCredsClientPass());
        issueCredentialsAuthConf.setIssuer(config.issueCredsPartnerCode());

        this.issueCredentialsConf = issueCredentialsAuthConf;
    }

    private OptionalResponse<String> getAuthToken(
    ) {
        String appId = config.regprocAppId();
        String clientId = config.regprocClientId();
        String clientPass = config.regprocClientPass();


        try {
            String authToken = apiRequestService.getAuthToken(
                    appId,
                    clientId,
                    clientPass
            );
            return optional(authToken);
        } catch (IOException | InterruptedException ex) {
            logger.error("Failed to get auth token");
            logger.error(ex.getMessage());

            MosipResponseError mre = new MosipResponseError();
            mre.setErrorCode("REQ1");
            mre.setMessage("Failed to get auth token");
            return optional(mre);
        }
    }

    private OptionalResponse<String> generateTokenId(String uid, String authToken) {
        String partnerCode = config.regprocPartnerCode();
        try {
            String tokenId = apiRequestService.generateTokenId(uid, partnerCode, authToken);
            return optional(tokenId);
        } catch (Exception ex) {
            logger.error("Failed to get  token id");
            logger.error(ex.getMessage());
            MosipResponseError mre = new MosipResponseError();
            mre.setErrorCode("REQ2");
            mre.setMessage("Failed to get token id");
            return optional(mre);
        }
    }

    @Override
    public EventConsumerResponse processRecord(DecryptedOnDemandTemplateRecord record) {
        String uid = record.getId();
        String partnerCode = config.regprocPartnerCode();
        OptionalResponse<String> authTokenOpt = getAuthToken();
        String authToken = authTokenOpt.isValid() ? authTokenOpt.data : null;
        if (!authTokenOpt.isValid()) {
            return eventConsumerResponse(EventConsumerStatus.ERROR);
        }
        OptionalResponse<String> tokenIdOpt = generateTokenId(uid, authToken);
        if (!tokenIdOpt.isValid()) {
            return eventConsumerResponse(EventConsumerStatus.ERROR);
        }
        String tokenId = tokenIdOpt.getData();
        OptionalResponse<CredentialRequestAdditionalDataDto> credentialRequestAdditionalDataOpt =
                this.getAdditionalData(
                        uid,
                        record.getIdType(),
                        tokenId
                );

        if (!credentialRequestAdditionalDataOpt.isValid()) {
            logger.error(credentialRequestAdditionalDataOpt.getError().getMessage());
            return eventConsumerResponse(EventConsumerStatus.ERROR);
        }
        CredentialRequestAdditionalDataDto credentialRequestAdditionalData = credentialRequestAdditionalDataOpt.getData();

        OptionalResponse<String> requestIdOpt = getRequestId(
                authToken,
                uid,
                credentialRequestAdditionalData
        );
        if (!requestIdOpt.isValid()) {
            return processRecordHandleError(requestIdOpt);
        }
        String requestId = requestIdOpt.getData();

        OptionalResponse<String> issueCredAuthTokenOpt = getIssueCredAuthToken();
        if (!issueCredAuthTokenOpt.isValid()) {
            return processRecordHandleError(requestIdOpt);
        }

        try {
            IssueCredentialsRawResponseDto rawResponse = apiRequestService.issueCredentials(
                    issueCredAuthTokenOpt.getData(),
                    uid,
                    partnerCode,
                    requestId,
                    credentialRequestAdditionalData
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
                String issueStatus = response.getStatus();
                logger.debug("Status = " + issueStatus);
            }
        } catch (Exception e) {
            logger.error("Error = " + e);
            return eventConsumerResponse(EventConsumerStatus.ERROR);
        }
        return eventConsumerResponse(EventConsumerStatus.SUCCESS);
    }

    private EventConsumerResponse processRecordHandleError(OptionalResponse<?> resp) {
        logger.error(resp.getError().getMessage());
        return eventConsumerResponse(EventConsumerStatus.ERROR);
    }

    private OptionalResponse<String> getIssueCredAuthToken() {

        try {
            String issueCredAuthToken = apiRequestService.getAuthToken(
                    issueCredentialsConf.getAppId(),
                    issueCredentialsConf.getClientId(),
                    issueCredentialsConf.getClientPass()
            );
            return optional(issueCredAuthToken);
        }  catch (IOException | InterruptedException ex) {
            logger.debug(ex.getMessage());
            MosipResponseError mre = new MosipResponseError();
            mre.setMessage("Failed to get issueCredAuthToken ");
            mre.setErrorCode("ISSUECREDAUTHTOKEN:get");
            return optional(mre);
        }
    }

    private OptionalResponse<String> getRequestId(
            String authToken,
            String uid,
            CredentialRequestAdditionalDataDto credentialRequestAdditionalData
    ) {
        try {
            String requestId = apiRequestService.getCredentialRequestId(
                    authToken,
                    uid,
                    issueCredentialsConf.getIssuer(),
                    credentialRequestAdditionalData
            );
            return optional(requestId);
        } catch (IOException | InterruptedException ex) {
            MosipResponseError mre = new MosipResponseError();
            mre.setErrorCode("REQ:GET_CREDENTIAL_REQUEST_ID");
            mre.setMessage("Failed to get requestId");
            logger.error("{}:{}", mre.getErrorCode(), mre.getMessage());
            logger.error(ex.getMessage());
            return optional(mre);
        }
    }
    private EventConsumerResponse eventConsumerResponse(EventConsumerStatus status) {
        EventConsumerResponse ecr = new EventConsumerResponse();
        ecr.setStatus(EventConsumerStatus.SUCCESS);
        return ecr;
    }

    private OptionalResponse<CredentialRequestAdditionalDataDto> getAdditionalData(
            final String id,
            final String idType,
            final String tokenId
    ) {
        String salt = this.saltUtil.getSaltForVid(id);
        String expiryTimestamp = this.config.expiryTimestamp();

        int modulo;
        try {
            modulo = this.saltUtil.calculateModulo(id);
        } catch(NoSuchAlgorithmException ex) {
            logger.error(ex.getMessage());
            MosipResponseError mre = new MosipResponseError();
            mre.setErrorCode("SALTUTIL:CALCULATE_MODULO");
            mre.setMessage("Wrong Algorithm specified for calculating modulo of salt");
            return optional(mre);
        }


        String idHash = null;
        try {
            idHash = generateIdHash(id, salt);
        } catch (NoSuchAlgorithmException ex) {
            logger.debug("Warn: %s", ex);
        }
        CredentialRequestAdditionalDataDto additionalData = new CredentialRequestAdditionalDataDto();
        additionalData.setIdType(idType);
        additionalData.setTokenId(tokenId);
        additionalData.setModulo(Integer.toString(modulo));
        additionalData.setSalt(salt);
        additionalData.setExpiryTimestamp(expiryTimestamp);
        additionalData.setIdHash(idHash);
        return optional(additionalData);
    }



    private <T> OptionalResponse<T> optional(MosipResponseError error) {
        if (error != null)
            return new OptionalResponse<>(OptionalResponse.Status.ERROR, null, error);
        MosipResponseError mre = new MosipResponseError();
        mre.setErrorCode(
                "1"
        );
        mre.setMessage("error is null in optional response, this is bad bad programming by developer, defaulting to default error");
        return new OptionalResponse<>(OptionalResponse.Status.ERROR, null, mre);
    }

    private <T> OptionalResponse<T> optional(T data) {
        if (data != null)
            return new OptionalResponse<T>(OptionalResponse.Status.VALID, data, null);

        MosipResponseError mre = new MosipResponseError();
        mre.setErrorCode(
                "2"
        );
        mre.setMessage("Both the data is null in optional response, this is bad bad programming by developer");
        return new OptionalResponse<>(OptionalResponse.Status.ERROR, null, mre);
    }

    @Data
    static class OptionalResponse<Value> {
        public enum Status {
            ERROR, VALID
        }
        public boolean isValid() {
            return this.status != Status.ERROR;
        }

        private OptionalResponse(Status status, Value data, MosipResponseError error) {
            this.status = status;
            this.data = data;
            this.error = error;
        }

        private final Status status;
        private final Value data;
        private final MosipResponseError error;
    }
}
