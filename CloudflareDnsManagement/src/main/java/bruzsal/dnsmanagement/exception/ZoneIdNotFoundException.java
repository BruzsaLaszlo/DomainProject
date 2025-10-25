package bruzsal.dnsmanagement.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ZoneIdNotFoundException extends RuntimeException {

    public ZoneIdNotFoundException(String message) {
        super(message);
    }

}
