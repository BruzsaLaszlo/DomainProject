package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.dto.CloudflareResultDto;
import bruzsal.dnsmanagement.dto.CloudflareResultListDto;
import bruzsal.dnsmanagement.dto.ZoneDto;
import bruzsal.dnsmanagement.exception.ZoneNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
//@RequiredArgsConstructor
public class ZoneService {

    private final RestClient.Builder baseRestClientBuilder;
    private final UserSession userSession;

    public ZoneService(RestClient.Builder baseRestClientBuilder, UserSession userSession) {
        this.baseRestClientBuilder = baseRestClientBuilder;
        this.userSession = userSession;
    }

    /**
     * Létrehoz egy RestClient példányt az aktuális session API tokenjével.
     */
    private RestClient createClientWithApiToken() {
        return baseRestClientBuilder
                .baseUrl("https://api.cloudflare.com/client/v4/zones/")
                .defaultHeader("Authorization", "Bearer %s".formatted(userSession.getApiToken()))
                .build();
    }

    public ZoneDto getZoneById(String id) {
        if (id == null) {
            throw new ZoneNotFoundException("Zone ID is null");
        }
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .uri("{id}", id)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.isSameCodeAs(NOT_FOUND), (request, response) -> {
                    throw new ZoneNotFoundException("zoneID : " + userSession.getZoneId());
                })
                .body(new ParameterizedTypeReference<CloudflareResultDto<ZoneDto>>() {
                })).result();
    }

    public ZoneDto getZoneByName(String name) {
        ZoneDto found = getAllZone().stream()
                .filter(zone -> name.equals(zone.name()))
                .findFirst()
                .orElseThrow(() -> new ZoneNotFoundException("Zone can not found by name: " + name));
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .uri("{id}", found.id())
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultDto<ZoneDto>>() {
                })).result();
    }

    public List<ZoneDto> getAllZone() {
        return Objects.requireNonNull(createClientWithApiToken()
                .get()
                .retrieve()
                .body(new ParameterizedTypeReference<CloudflareResultListDto<ZoneDto>>() {
                })).result();
    }
}
