package bruzsal.dnsmanagement.exception;

import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static java.util.stream.Collectors.joining;

@Slf4j
@Getter
public class DnsRecordException extends RuntimeException {

    private final String title;
    private final String detail;

    public DnsRecordException(CloudflareErrorDto cloudflareErrorDto) {
        title = cloudflareErrorDto
                .errors()
                .stream()
                .map(CloudflareErrorDto.Error::message)
                .collect(joining("\n"));
        if (cloudflareErrorDto.messages() == null || cloudflareErrorDto.messages().isEmpty()) {
            detail = "";
        } else {
            detail = cloudflareErrorDto
                    .messages()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(joining("\n"));
        }
        log.info("{}: {}", title, detail);
    }
}
