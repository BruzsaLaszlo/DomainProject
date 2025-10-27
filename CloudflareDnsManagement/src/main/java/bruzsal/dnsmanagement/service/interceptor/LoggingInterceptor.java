package bruzsal.dnsmanagement.service.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        // A válasz törzsének logolásához be kell csomagolnunk,
        // hogy a stream többször is olvasható legyen.
        ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response);
        logResponse(bufferedResponse);
        return bufferedResponse;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.debug("===========================request begin================================================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        log.debug("Headers     : {}", request.getHeaders());
        if (body.length > 0) {
            log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
        }
        log.debug("==========================request end=================================================");
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.debug("============================response begin==========================================");
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        log.debug("Headers      : {}", response.getHeaders());
        log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
        log.debug("=======================response end=================================================");
    }
}