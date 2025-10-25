package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.TestSessionConfig;
import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.dto.model.DnsRecordDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSessionConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DnsRecordControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    static final String TYPE = "TXT";
    static final String NAME = "test.dnsrecordcontrollerit" + new Random().nextLong();
    static final String CONTENT = "\"test createDnsRecords\"";

    String dnsRecordId;

    @Test
    void getAllRecordTest() {
        webTestClient
                .get()
                .uri("/api/records")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DnsRecordDto.class);
    }

    @BeforeAll
    void createDnsRecords() {
        DnsRecordCommand dnsRecordCommand = new DnsRecordCommand(TYPE, NAME + "." + zoneName, CONTENT);
        DnsRecordDto dnsRecordDto = webTestClient
                .post()
                .uri("/api/records")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dnsRecordCommand)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DnsRecordDto.class)
                .returnResult()
                .getResponseBody();
        Assertions.assertNotNull(dnsRecordDto);
        dnsRecordId = dnsRecordDto.id();
    }

    @Test
    void deleteTestDnsRecords() {
        webTestClient
                .delete()
                .uri("/api/records/{id}", dnsRecordId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> assertEquals(dnsRecordId, response));

        webTestClient
                .delete()
                .uri("/api/records/{id}", "invalid_id")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ProblemDetail.class);
    }

    @Test
    @Disabled("Warning DELETE all dns record which start with `test`")
    void deleteAllTestDnsRecords() {
        List<DnsRecordDto> list = webTestClient
                .get()
                .uri("/api/records/filterByNameStartsWith?prefix=test")
                .exchange()
                .expectBodyList(DnsRecordDto.class)
                .returnResult().getResponseBody();
        assertThat(list).isNotEmpty();
        list.forEach(dnsRecord ->
                webTestClient
                        .delete()
                        .uri("/api/records/{id}", dnsRecord.id())
                        .exchange()
                        .expectStatus().isOk());
    }

}