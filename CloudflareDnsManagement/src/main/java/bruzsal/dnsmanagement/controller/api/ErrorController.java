package bruzsal.dnsmanagement.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/error")
public class ErrorController {

    @GetMapping("/authorization")
    public String error() {
        return "ERROR: Cloudflare API Token is invalid\r\nYou need to set valid api token: ${server}/api/zones/api-token";
    }

}
