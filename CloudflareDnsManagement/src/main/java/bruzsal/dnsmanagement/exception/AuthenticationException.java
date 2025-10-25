package bruzsal.dnsmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

@AllArgsConstructor
@Getter
public class AuthenticationException extends RuntimeException {

    private final transient HttpRequest request;
    private final transient ClientHttpResponse response;

}
