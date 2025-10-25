package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class LoginController {

    private final UserSession userSession;

    @PostMapping("/login")
    public String login(@RequestParam String username) {
        userSession.setUsername(username);
        return "Logged in: " + username;
    }

}
