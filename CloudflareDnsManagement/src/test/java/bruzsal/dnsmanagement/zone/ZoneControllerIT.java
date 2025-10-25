package bruzsal.dnsmanagement.zone;

import bruzsal.dnsmanagement.TestSessionConfig;
import bruzsal.dnsmanagement.dto.ZoneDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSessionConfig.class)
@Log4j2
class ZoneControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Value("${cloudflare.api-token}")
    String apiToken;

    @Value("${cloudflare.zone-id}")
    String zoneId;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    @Test
    void getZoneDetailTest() {
        webTestClient
                .get()
                .uri("/api/zones/actual")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ZoneDto.class)
                .hasSize(1);
    }

    @Test
    void getZoneByName() {
        webTestClient
                .get()
                .uri("/api/zones/{name}", zoneName)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ZoneDto.class)
                .value(zones -> assertThat(zones)
                        .hasSizeGreaterThanOrEqualTo(1)
                        .first()
                        .extracting(ZoneDto::name)
                        .isEqualTo(zoneName));
    }

    @Test
    void getZoneByInvalidName() {
        webTestClient
                .get()
                .uri("/api/zones/name/invalidName.hu")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class);
    }

}