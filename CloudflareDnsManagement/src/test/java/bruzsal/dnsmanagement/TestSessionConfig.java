package bruzsal.dnsmanagement;

import bruzsal.dnsmanagement.controller.session.UserSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@TestConfiguration
public class TestSessionConfig {

    @Value("${cloudflare.api-token}")
    protected String apiToken;

    @Value("${cloudflare.zone-id}")
    protected String zoneId;

    @Bean
    @Primary
    @Scope("prototype") // or "singleton" for tests
    public UserSession testUserSession() {
        UserSession userSession = new UserSession();
        userSession.setApiToken(apiToken);
        userSession.setZoneId(zoneId);
        return userSession;
    }

}
