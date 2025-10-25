package bruzsal.dnsmanagement.exception;

public class ZoneNotFoundException extends RuntimeException {
    public ZoneNotFoundException(String message) {
        super(message);
    }
}
