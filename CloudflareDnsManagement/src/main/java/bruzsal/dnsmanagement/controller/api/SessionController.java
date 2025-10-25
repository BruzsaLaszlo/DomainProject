package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.controller.validation.ValidApiToken;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final UserSession userSession;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam @NotBlank String username) {
        userSession.setUsername(username);
        return ResponseEntity.ok("Logged in: " + username);
    }

    @GetMapping("/user")
    public ResponseEntity<String> getLoggedInUser() {
        return ResponseEntity.ok("Logged in as: " + userSession.getUsername());
    }

    @PostMapping("/api-token")
    public ResponseEntity<String> setApiToken(@RequestParam @ValidApiToken String apiToken) {
        userSession.setApiToken(apiToken);
        return ResponseEntity.ok("API token set");
    }

    @PostMapping("/zone-id")
    public ResponseEntity<String> setZoneId(@RequestParam String zoneId) {
        userSession.setZoneId(zoneId);
        return ResponseEntity.ok("ZoneID set");
    }

    @PostMapping("/logout")
    public String clearSession(HttpSession session) {
        session.invalidate();
        return "Session cleared";
    }

}