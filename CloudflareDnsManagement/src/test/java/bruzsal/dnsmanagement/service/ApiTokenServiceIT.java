package bruzsal.dnsmanagement.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTokenServiceIT {

    @Autowired
    ApiTokenService apiTokenService;

    @Value("${cloudflare.api-token}")
    String apiToken;

    @Test
    void verifyToken() {
        System.out.println(apiToken);
        apiTokenService.verifyToken(apiToken);
    }

}