package bruzsal.dnsmanagement.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiTokenNotFoundException extends RuntimeException {

    public ApiTokenNotFoundException(String message) {
        super(message);
    }

}
