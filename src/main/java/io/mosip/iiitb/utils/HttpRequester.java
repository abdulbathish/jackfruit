package io.mosip.iiitb.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HttpRequester {
    private static final int REQUEST_TIMEOUT = 5; // Timeout in seconds

    private HttpClient client;
    private Gson gson;

    public HttpRequester() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .build();
        this.gson = new Gson();
    }

    public ResponseWrapper getRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return executeRequest(request);
    }

    public ResponseWrapper makePostRequest(String url, Object data) throws Exception {
        String json = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return executeRequest(request);
    }

    private ResponseWrapper executeRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Request failed with status: " + response.statusCode());
        }

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> responseBody = gson.fromJson(response.body(), type);

        return new ResponseWrapper(responseBody, response.headers());
    }



    @Getter
    public static class ResponseWrapper {
        private Map<String, Object> response;
        private HttpHeaders headers;

        public ResponseWrapper(Map<String, Object> response, HttpHeaders headers) {
            this.response = response;
            this.headers = headers;
        }

    }
}