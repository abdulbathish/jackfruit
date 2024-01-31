package io.mosip.iiitb.lib;

import io.mosip.iiitb.utils.HttpRequester;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiRequestService {
    private final HttpRequester httpRequester;
    private final URI baseUri;

    final String RUNNING_SERVER_URL = "https://qa3.mosip.net";
    final String CREDENTIAL_REQUEST_GENERATOR_USER = "dolphin-service-account-mosip-regproc-client";

    public ApiRequestService() {
        this.baseUri = URI.create(RUNNING_SERVER_URL);
        this.httpRequester = new HttpRequester();
    }

    /**
     * @param id VID | UIN
     * @return
     */
    public String generateTokenId(String id, String partnerCode, String token) throws Exception {
        URI url = baseUri.resolve(String.format("/v1/keymanager/%s/%s", id, partnerCode));
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
     * @param tokenId
     * @param id = VID | UIN
     * @param idType = "VID" | "UIN"
     * @param issuer = auth partner id
     * @param
     * @return
     */
    public String getCredentialRequestId(
            String authToken,
            String tokenId,
            String id,
            String idType,
            String issuer
    ) throws IOException, InterruptedException {
        URI url = baseUri.resolve("/v1/credentialrequest/requestgenerator");
        HttpCookie cookie = getAuthCookie(authToken);
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("id", id);
        authRequest.put("credentialType", "auth");
        authRequest.put("issuer", issuer);
        authRequest.put("recipient", "IDA");
        authRequest.put("user", CREDENTIAL_REQUEST_GENERATOR_USER);
        authRequest.put("encrypt", false);
        authRequest.put("sharableAttributes", new ArrayList<>());

        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("SALT", "");
        additionalData.put("expiry_timestamp", "");
        additionalData.put("idType", idType);
        additionalData.put("MODULO", "420");
        additionalData.put("transaction_limit", "");
        additionalData.put("TOKEN", tokenId);

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
        CredentialRequestGeneratorResponseDto response = httpResponse.getBody().response;
        String requestId = response.getRequestId();
        return requestId;
    }

    public IssueCredentialsResponseDto issueCredentials(
            String authToken,
            String id,
            String idType,
            String issuer,
            String requestId
    ) throws IOException, InterruptedException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("credentialType", "auth");
        body.put("issuer", issuer);
        body.put("requestId", requestId);
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("idType", idType);
        body.put("additionalData", additionalData);
        URI uri = baseUri.resolve("/v1/credentialservice/issue");
        HttpRequester.ResponseWrapper<IssueCredentialsRawResponseDto> httpResponse = httpRequester.makePostRequest(
            uri.toString(),
            body,
            getAuthCookie(authToken),
            IssueCredentialsRawResponseDto.class
        );
        IssueCredentialsResponseDto response = httpResponse.getBody().getResponse();
        return response;
    }
    public String getAuthToken(String appId, String clientId, String clientPass)
            throws IOException, InterruptedException {
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

        URI uri = baseUri.resolve("/v1/authmanager/authenticate/clientidsecretkey");
        HttpRequester.ResponseWrapper<GetAuthTokenResponseDto> response = httpRequester.makePostRequest(
                uri.toString(),
                requestBody,
                null,
                GetAuthTokenResponseDto.class
        );
        String authToken = readToken(response.getHeaders());
        return authToken;
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
}
