package bruzsal.dnsmanagement.exception;

import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException {

    private final transient CloudflareErrorDto error;

    public ResponseException(CloudflareErrorDto cloudflareErrorDto) {
        super(cloudflareErrorDto.toString());
        this.error = cloudflareErrorDto;
    }

}
