package io.mosip.iiitb.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;

public class HttpRequester {
    private static final int REQUEST_TIMEOUT = 5; // Timeout in seconds

    private final HttpClient client;
    private final Gson gson;

    public HttpRequester() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .build();
        this.gson = new Gson();
    }

    public <T> ResponseWrapper<T> getRequest(String url, HttpCookie cookie, Class<T> clazz)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .header("Content-Type", "application/json");
        if (cookie != null) {
            builder.header("Cookie", cookie.toString());
        }
        HttpRequest request =
                builder
                        .GET()
                        .build();
        return executeRequest(request, clazz);
    }

    public <T> ResponseWrapper<T> makePostRequest(String url, Object data, HttpCookie cookie, Class<T> clazz) throws IOException, InterruptedException {
        String json = gson.toJson(data);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT))
                .header("Content-Type", "application/json");
        if (cookie != null) {
            builder.header("Cookie", cookie.toString());
        }

        HttpRequest request =
                builder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return executeRequest(request, clazz);
    }


    private <T> ResponseWrapper<T> executeRequest(HttpRequest request, Class<T> clazz)
            throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T responseBody = objectMapper.readValue(response.body(), clazz);

        return new ResponseWrapper<>(responseBody, response.headers());
    }

    @Getter
    public static class ResponseWrapper<T> {
        private final T body;
        private final HttpHeaders headers;

        public ResponseWrapper(T response, HttpHeaders headers) {
            this.body = response;
            this.headers = headers;
        }
    }
}