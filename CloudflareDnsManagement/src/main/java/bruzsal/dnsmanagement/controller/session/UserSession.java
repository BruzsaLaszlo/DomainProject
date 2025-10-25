package bruzsal.dnsmanagement.controller.session;


import bruzsal.dnsmanagement.exception.ApiTokenNotFoundException;
import bruzsal.dnsmanagement.exception.ZoneIdNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class UserSession implements Serializable {

    private String username;

    private String apiToken;

    private String zoneId;

    public String getApiToken() {
        if (apiToken == null || apiToken.isBlank()) {
            throw new ApiTokenNotFoundException();
        }
        return apiToken;
    }

    public String getZoneId() {
        if (zoneId == null || zoneId.isBlank()) {
            throw new ZoneIdNotFoundException("Zone ID not set in session");
        }
        return zoneId;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "username='" + username + '\'' +
                ", apiToken='" + apiToken + '\'' +
                ", zoneId='" + zoneId + '\'' +
                '}';
    }
}

