package io.mosip.iiitb.lib;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
import io.mosip.iiitb.dto.*;
import io.mosip.iiitb.utils.HttpRequester;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ApiRequestService {
    private final HttpRequester httpRequester;
    private final URI baseUri;
    private final String authEndpoint;
    private final String keyManagerTokenIdEndpoint;
    private final String credentialRequestEndpoint;
    private final String CredentialIssuanceEndpoint;
    private final String authCredentialType;
    private final String authRecipient;
    private final String RUNNING_SERVER_URL;
    private final String CREDENTIAL_REQUEST_GENERATOR_USER;

    private final AuthTokenCache authTokenCache;

    @Inject
    public ApiRequestService(
            OnDemandAppConfig config,
            HttpRequester httpRequester
    ) throws IllegalArgumentException {
        // Initialize configuration properties
        this.RUNNING_SERVER_URL = config.mosipServerUrl();
        this.keyManagerTokenIdEndpoint = config.keyManagerTokenIdEndpoint();
        this.credentialRequestEndpoint = config.credentialRequestEndpoint();
        this.CredentialIssuanceEndpoint = config.credentialIssuanceEndpoint();
        this.authEndpoint = config.authEndpoint();
        this.authCredentialType = config.authCredentialType();
        this.authRecipient = config.authRecipient();
        this.CREDENTIAL_REQUEST_GENERATOR_USER = config.credentialRequestGeneratorUser();

        validateConfigProperties();

        this.baseUri = URI.create(RUNNING_SERVER_URL);
        this.httpRequester = httpRequester;
        this.authTokenCache = new AuthTokenCache(null, 0);
    }

    /**
     * Validates that all required configuration properties are present and have valid values.
     * Throws an IllegalArgumentException if any required property is missing or invalid.
     */
    private void validateConfigProperties() {
        if (RUNNING_SERVER_URL == null || RUNNING_SERVER_URL.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'mosipServerUrl' configuration property.");
        }
        if (keyManagerTokenIdEndpoint == null || keyManagerTokenIdEndpoint.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'keyManagerTokenIdEndpoint' configuration property.");
        }
        if (credentialRequestEndpoint == null || credentialRequestEndpoint.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'credentialRequestEndpoint' configuration property.");
        }
        if (CredentialIssuanceEndpoint == null || CredentialIssuanceEndpoint.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'CredentialIssuanceEndpoint' configuration property.");
        }
        if (authEndpoint == null || authEndpoint.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'authEndpoint' configuration property.");
        }
        if (authCredentialType == null || authCredentialType.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'authCredentialType' configuration property.");
        }
        if (authRecipient == null || authRecipient.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'authRecipient' configuration property.");
        }
        if (CREDENTIAL_REQUEST_GENERATOR_USER == null || CREDENTIAL_REQUEST_GENERATOR_USER.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'credentialRequestGeneratorUser' configuration property.");
        }
    }

    /**
     * @param uid VID | UIN
     * @return
     */
    public String generateTokenId(String uid, String partnerCode, String token) throws Exception {
        URI url = baseUri.resolve(String.format(keyManagerTokenIdEndpoint, uid, partnerCode));
        HttpCookie cookie = getAuthCookie(token);
        GenerateTokenIdRawResponseDto body = this.httpRequester.getRequest(
            url.toString(),
            cookie,
            GenerateTokenIdRawResponseDto.class
        ).getBody();
        String tokenId = body.getResponse().getTokenID();
        return tokenId;
    }

    /**
     * @param authToken = Authorization Token set in cookie header
     * @param uid = VID | UIN
     * @param issuer = auth partner id
     * @param
     * @return
     */
    public String getCredentialRequestId(
            String authToken,
            String uid,
            String issuer,
            CredentialRequestAdditionalDataDto additionalData
    ) throws IOException, InterruptedException {
        URI url = baseUri.resolve(credentialRequestEndpoint);
        HttpCookie cookie = getAuthCookie(authToken);
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("id", uid);
        authRequest.put("recipient", authRecipient);
        authRequest.put("issuer", issuer);
        authRequest.put("credentialType", authCredentialType);
        authRequest.put("user", CREDENTIAL_REQUEST_GENERATOR_USER);
        authRequest.put("encrypt", false); //set property
        authRequest.put("sharableAttributes", null);
        authRequest.put("additionalData", additionalData);

        Map<String, Object> body = new HashMap<>();
        body.put("id", "mosip.credentialrequest");
        body.put("request", authRequest);
        body.put("requestTime", getTimeStamp());
        body.put("version", "1.0");

        HttpRequester.ResponseWrapper<CredentialRequestGeneratorRawResponseDto> httpResponse = httpRequester.makePostRequest(
            url.toString(),
            body,
            cookie,
            CredentialRequestGeneratorRawResponseDto.class
        );
        CredentialRequestGeneratorResponseDto response = httpResponse.getBody().getResponse();
        String requestId = response.getRequestId();
        return requestId;
    }

    public IssueCredentialsRawResponseDto issueCredentials(
        String authToken,
        String id,
        String issuer,
        String requestId,
        CredentialRequestAdditionalDataDto additionalData
    ) throws IOException, InterruptedException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("credentialType", authCredentialType);
        body.put("issuer", issuer);
        body.put("requestId", requestId);
        body.put("additionalData", additionalData);
        URI uri = baseUri.resolve(CredentialIssuanceEndpoint);
        HttpRequester.ResponseWrapper<IssueCredentialsRawResponseDto> httpResponse = httpRequester.makePostRequest(
            uri.toString(),
            body,
            getAuthCookie(authToken),
            IssueCredentialsRawResponseDto.class
        );
        IssueCredentialsRawResponseDto responseBody = httpResponse.getBody();
        return responseBody;
    }


    public String getAuthToken(String appId, String clientId, String clientPass)
            throws IOException, InterruptedException {
        if (authTokenCache.getValue() != null)
            return authTokenCache.getValue();

        String timestamp = getTimeStamp();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", "mosip.io.clientId.pwd");
        requestBody.put("metadata", new HashMap<>());
        requestBody.put("version", "1.0");
        requestBody.put("requesttime", timestamp);
        Map<String, String> requestData = new HashMap<>();
        requestData.put("appId", appId);
        requestData.put("clientId", clientId);
        requestData.put("secretKey", clientPass);
        requestBody.put("request",  requestData);

        URI uri = baseUri.resolve(authEndpoint);
        HttpRequester.ResponseWrapper<GetAuthTokenResponseDto> response = httpRequester.makePostRequest(
                uri.toString(),
                requestBody,
                null,
                GetAuthTokenResponseDto.class
        );

        HashMap<String, String> headerKVs = parseCookieFields(
                response.getHeaders(),
                new String[]{"authorization", "max-age"}
        );
        String authToken = headerKVs.get("authorization");
        String maxAge = headerKVs.get("max-age");

        boolean isCacheSet = authTokenCache.setValue(
                authToken,
                Long.parseLong(maxAge)
        );
        return authToken;
    }

    private HashMap<String, String> parseCookieFields(HttpHeaders headers, String[] keys) {
        HashMap<String, String> result = new HashMap<String, String>();
        String setCookieHeader = headers.firstValue("Set-Cookie").orElse("");
        String[] cookies = setCookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.split("=");
            String cookieKey = parts.length > 1 ? parts[0].trim().toLowerCase() : "NO_PROPER_KEY";
            if (cookieKey.equals("NO_PROPER_KEY"))
                continue;
            String cookieValue = parts[1];
            for (String givenKey: keys)
                if (cookieKey.equals(givenKey))
                    result.put(givenKey, cookieValue);
        }
        return result;
    }

    private HttpCookie getAuthCookie(String token) {
        return new HttpCookie("Authorization", token);
    }
    private String getTimeStamp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        return zonedDateTime.format(formatter);
    }
    private String readToken(HttpHeaders headers) {
        String setCookieHeader = headers.firstValue("Set-Cookie").orElse("");
        String[] cookies = setCookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.split("=");
            if (parts.length > 1 && parts[0].trim().equals("Authorization")) {
                return parts[1];
            }
        }
        return null;
    }

    private class AuthTokenCache {

        private String value;
        private Instant cachedAt;
        private long maxAgeInSeconds;

        public AuthTokenCache(String value, long maxAgeInSeconds) {
            this.value = value;
            this.cachedAt = Instant.now();
            this.maxAgeInSeconds = maxAgeInSeconds;
        }

        public String getValue() {
            if (value == null)
                return null;
            Instant expirationTime = cachedAt.plus(maxAgeInSeconds, ChronoUnit.SECONDS);
            Instant currentTime = Instant.now();
            if (currentTime.isBefore(expirationTime)) {
                return value;
            } else {
                return null;
            }
        }

        public boolean setValue(String value, long maxAgeInSeconds) {
            this.value = value;
            this.cachedAt = Instant.now();
            this.maxAgeInSeconds = maxAgeInSeconds;
            return true;
        }
    }
}
