package bruzsal.dnsmanagement.exception;


public class DnsRecordAmbiguousException extends RuntimeException {
    public DnsRecordAmbiguousException(String message) {
        super(message);
    }
}
