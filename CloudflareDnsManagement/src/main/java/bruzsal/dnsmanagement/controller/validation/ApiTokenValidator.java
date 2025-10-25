package bruzsal.dnsmanagement.controller.validation;

import bruzsal.dnsmanagement.dto.CloudflareTokenResultDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class ApiTokenValidator implements ConstraintValidator<ValidApiToken, String> {

    @Override
    public boolean isValid(String apiToken, ConstraintValidatorContext context) {
        try {
            CloudflareTokenResultDto b = RestClient.builder()
                    .baseUrl("https://api.cloudflare.com/client/v4/user/tokens/verify")
                    .defaultHeaders(headers -> {
                        headers.add("Authorization", "Bearer " + apiToken);
                        headers.add("Content-Type", "application/json");
                    })
                    .build()
                    .get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            return b != null && b.success();
        } catch (Exception _) {
            return false;
        }
    }

}