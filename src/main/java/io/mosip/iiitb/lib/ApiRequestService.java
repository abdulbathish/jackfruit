package io.mosip.iiitb.lib;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.mosip.iiitb.utils.HttpRequester;
import org.apache.kafka.common.protocol.types.Field;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ApiRequestService {
    private HttpRequester httpRequester;
    private URI baseUri;

    private Gson gson;

    final String RUNNING_SERVER_URL = "https://qa3.mosip.net";

    public ApiRequestService() {
        this.baseUri = URI.create(RUNNING_SERVER_URL);
        this.httpRequester = new HttpRequester();
        this.gson = new Gson();
    }

    public String generateTokenId() {
        HttpCookie cookie = new HttpCookie("test-cookie-one", "");
        cookie.


    }
    public String getAuthToken(String appId, String clientId, String clientPass) throws Exception {
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
        HttpRequester.ResponseWrapper response = httpRequester.makePostRequest(uri.toString(), requestBody);
        String authToken = readToken(response.getHeaders());

        return authToken;
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
