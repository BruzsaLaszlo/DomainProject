package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.dto.CloudflareTokenResultDto;
import bruzsal.dnsmanagement.exception.ApiTokenInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ApiTokenService {

    public void verifyToken(String token) {
        CloudflareTokenResultDto b = RestClient.builder()
                .baseUrl("https://api.cloudflare.com/client/v4/user/tokens/verify")
                .defaultHeaders(headers -> {
                    headers.add("Authorization", "Bearer " + token);
                    headers.add("Content-Type", "application/json");
                })
                .build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (b == null || !b.success()) {
            throw new ApiTokenInvalidException();
        }
    }

}
