package bruzsal.dnsmanagement.service.alt;

import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import bruzsal.dnsmanagement.exception.ResponseException;
import bruzsal.dnsmanagement.service.httpclient.MyObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static bruzsal.dnsmanagement.service.alt.HttpClientService.Method.*;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpClientService {

    private final HttpClient httpClient;
    private final MyObjectMapper om;

    public enum Method {GET, PUT, PATCH, POST, DELETE}

    public String get(URI uri, String apiToken) {
        return crud(uri, GET, null, apiToken);
    }

    public String patch(URI uri, DnsRecordCommand dnsRecordCommand, String apiToken) {
        return crud(uri, PATCH, dnsRecordCommand, apiToken);
    }

    public String post(URI uri, DnsRecordCommand dnsRecordCommand, String apiToken) {
        return crud(uri, POST, dnsRecordCommand, apiToken);
    }

    public String delete(URI uri, String apiToken) {
        return crud(uri, DELETE, null, apiToken);
    }

    public String crud(URI uri, Method method, Object requestObject, String apiToken) {
        HttpRequest request = createRequest(uri, method, requestObject, apiToken);
        HttpResponse<String> response = sendRequest(request);
        String responseJson = response.body();
        if (response.statusCode() != OK.value()) {
            log.error(responseJson);
            throw new ResponseException(om.readValue(responseJson, CloudflareErrorDto.class));
        }
        log.info(responseJson);
        return responseJson;
    }

    private HttpRequest createRequest(URI uri, Method method, Object requestObject, String apiToken) {
        final String requestJson = om.writeValueAsString(requestObject);
        log.info("Create request: {}  body: {}", uri, requestJson);
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer %s".formatted(apiToken))
                .method(method.name(), ofString(requestJson))
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return httpClient.send(request, ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("HTTP client error", e);
            throw new IllegalStateException(e);
        } catch (IOException e) {
            log.error("HTTP client error", e);
            throw new IllegalStateException(e);
        }
    }
}
