package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.controller.request.DDnsCommand;
import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import bruzsal.dnsmanagement.dto.CloudflareResultDto;
import bruzsal.dnsmanagement.dto.CloudflareResultListDto;
import bruzsal.dnsmanagement.dto.DeleteDnsRecordDto;
import bruzsal.dnsmanagement.dto.DnsRecordDto;
import bruzsal.dnsmanagement.exception.DnsRecordAmbiguousException;
import bruzsal.dnsmanagement.exception.DnsRecordException;
import bruzsal.dnsmanagement.exception.DnsRecordNotFoundException;
import bruzsal.dnsmanagement.service.httpclient.MyObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class DnsRecordService {

    private final RestClient.Builder baseRestClientBuilder;
    private final UserSession userSession;
    private final MyObjectMapper om = new MyObjectMapper();

    @Autowired
    public DnsRecordService(RestClient.Builder baseRestClientBuilder, UserSession userSession) {
        this.baseRestClientBuilder = baseRestClientBuilder;
        this.userSession = userSession;
    }

    /**
     * Létrehoz egy RestClient példányt az aktuális session API tokenjével.
     */
    private RestClient createClientWithApiToken() {
        return baseRestClientBuilder
                .baseUrl("https://api.cloudflare.com/client/v4/zones/%s/dns_records/".formatted(userSession.getZoneId()))
                .defaultHeader("Authorization", "Bearer %s".formatted(userSession.getApiToken()))
                .build();
    }

    public List<DnsRecordDto> getAllRecords() {
        return getDnsRecordBy(empty(), empty(), empty());
    }

    public List<DnsRecordDto> getDnsRecordBy(Optional<String> type, Optional<String> name, Optional<String> content) {
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParamIfPresent("type", type)
                        .queryParamIfPresent("name", name)
                        .queryParamIfPresent("content.exact", content)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultListDto<DnsRecordDto>>() {
                })).result();
    }

    public DnsRecordDto getDnsSingleRecordBy(Optional<String> type, Optional<String> name, Optional<String> content) {
        List<DnsRecordDto> dnsRecordDtos = getDnsRecordBy(type, name, content);
        String message = "%s %s %s".formatted(type.orElse(""), name.orElse(""), content.orElse(""));
        if (dnsRecordDtos.isEmpty()) {
            throw new DnsRecordNotFoundException(message);
        } else if (dnsRecordDtos.size() > 1) {
            throw new DnsRecordAmbiguousException(message);
        }
        return dnsRecordDtos.getFirst();
    }

    public DnsRecordDto getDnsRecordById(String recordId) {
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .uri("{id}", recordId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (_, response) -> {
                    throw new DnsRecordNotFoundException("DNS not found with id: %s%n%s".formatted(recordId, response.getStatusText()));
                })
                .body(new ParameterizedTypeReference<CloudflareResultDto<DnsRecordDto>>() {
                })).result();
    }

    public DnsRecordDto updateDnsRecord(DnsRecordCommand dnsRecordCommand) {
        DnsRecordDto dnsRecordDtos = getDnsSingleRecordBy(ofNullable(dnsRecordCommand.getType()), ofNullable(dnsRecordCommand.getName()), empty());
        return Objects.requireNonNull(createClientWithApiToken()
                .patch()
                .uri("{id}", dnsRecordDtos.id())
                .body(dnsRecordCommand)
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultDto<DnsRecordDto>>() {
                })).result();
    }

    public DnsRecordDto createDnsRecord(DnsRecordCommand dnsRecordCommand) {
        return Objects.requireNonNull(createClientWithApiToken()
                .post()
                .body(dnsRecordCommand)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(BAD_REQUEST), (request, response) -> {
                    String responseJson = new String(response.getBody().readAllBytes());
                    log.error(responseJson);
                    CloudflareErrorDto cloudflareErrorDto = om.readValue(responseJson, CloudflareErrorDto.class);
                    throw new DnsRecordException(cloudflareErrorDto);
                })
                .body(new ParameterizedTypeReference<CloudflareResultDto<DnsRecordDto>>() {
                })).result();
    }

    public String deleteDnsRecord(String recordId) {
        return Objects.requireNonNull(createClientWithApiToken()
                .delete()
                .uri("{id}", recordId)
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultDto<DeleteDnsRecordDto>>() {
                })).result().id();
    }

    public List<DnsRecordDto> getDnsRecordNameStartsWith(Optional<String> prefix) {
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParamIfPresent("name.startswith", prefix)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultListDto<DnsRecordDto>>() {
                })).result();
    }

    public DnsRecordDto updateDnsIpAddress(DDnsCommand dDnsCommand) {
        List<DnsRecordDto> dnsRecordDtos = getDnsRecordBy(of(dDnsCommand.getType().value), of(dDnsCommand.getDomain()), empty());
        DnsRecordCommand dnsRecordCommand = new DnsRecordCommand(
                dDnsCommand.getType().value,
                dDnsCommand.getDomain(),
                dDnsCommand.getIpAddress());
        if (dnsRecordDtos.isEmpty()) {
            return createDnsRecord(dnsRecordCommand);
        } else {
            return updateDnsRecord(dnsRecordCommand);
        }
    }
}
