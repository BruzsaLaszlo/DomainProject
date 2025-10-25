package bruzsal.dnsmanagement.controller.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping("/user")
    public String getLoggedInUser(HttpSession session, Principal principal) {
        session.setAttribute("username", principal.getName());
        return "Logged in as: " + principal.getName();
    }

    @GetMapping("/session-value")
    public String getSessionValue(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return username != null ? "Session username: " + username : "No session value found";
    }

    @PostMapping("/clear")
    public String clearSession(HttpSession session) {
        session.invalidate();
        return "Session cleared";
    }
}