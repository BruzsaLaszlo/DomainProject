package bruzsal.dnsmanagement.config;

import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import bruzsal.dnsmanagement.exception.AuthenticationException;
import bruzsal.dnsmanagement.exception.DnsRecordException;
import bruzsal.dnsmanagement.service.httpclient.MyObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@Slf4j
public class RestClientConfig {

    @Bean
    public RestClient.Builder baseRestClientBuilder(MyObjectMapper om) {
        return RestClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(
                        httpStatusCode ->
                                httpStatusCode.isSameCodeAs(FORBIDDEN) || httpStatusCode.isSameCodeAs(UNAUTHORIZED),
                        (request, response) -> {
                            throw new AuthenticationException(request, response);
                        })
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    String responseJson = new String(response.getBody().readAllBytes());
                    log.error("""
                            Request URL: {}
                            Http Status Code: {}
                            Response: {}""", request.getURI(), response.getStatusCode(), responseJson);
                    CloudflareErrorDto cloudflareErrorDto = om.readValue(responseJson, CloudflareErrorDto.class);
                    throw new DnsRecordException(cloudflareErrorDto);
                });

    }


}
