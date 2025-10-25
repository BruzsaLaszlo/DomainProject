package bruzsal.dnsmanagement.exception;

public class DnsRecordNotFoundException extends RuntimeException {
    public DnsRecordNotFoundException(String message) {
        super(message);
    }

}
