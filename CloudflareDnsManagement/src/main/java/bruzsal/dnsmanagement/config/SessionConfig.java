package bruzsal.dnsmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession // Ez kényszeríti a Redis használatát
public class SessionConfig {
    // A konfigurációs osztálynak nem kell tartalmaznia semmit
}
