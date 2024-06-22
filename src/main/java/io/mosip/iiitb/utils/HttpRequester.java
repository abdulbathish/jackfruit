package io.mosip.iiitb.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import io.mosip.iiitb.config.OnDemandAppConfig;
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


    private final HttpClient client;

    private final Integer REQUEST_TIMEOUT_IN_SECONDS;

    @Inject
    public HttpRequester(
            OnDemandAppConfig config
    ) {
        this.REQUEST_TIMEOUT_IN_SECONDS = config.httpRequestTimeoutInSecs();
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(this.REQUEST_TIMEOUT_IN_SECONDS))
                .build();
    }

    public <T> ResponseWrapper<T> getRequest(String url, HttpCookie cookie, Class<T> clazz)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(this.REQUEST_TIMEOUT_IN_SECONDS))
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
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(this.REQUEST_TIMEOUT_IN_SECONDS))
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
        objectMapper.registerModule(new JavaTimeModule());
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
