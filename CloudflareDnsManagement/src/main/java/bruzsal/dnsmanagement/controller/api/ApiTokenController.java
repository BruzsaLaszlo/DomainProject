package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.service.ApiTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class ApiTokenController {

    private final ApiTokenService apiTokenService;
    private final UserSession userSession;

    @GetMapping
    public ResponseEntity<String> getToken() {
        return ResponseEntity.ok(userSession.getApiToken());
    }

    @GetMapping("/verify")
    public Boolean verifyToken() {
        apiTokenService.verifyToken(userSession.getApiToken());
        return true;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createToken(@RequestParam @Valid String apiToken) {
        apiTokenService.verifyToken(apiToken);
        userSession.setApiToken(apiToken);
        return ResponseEntity.ok(apiToken);
    }
}
