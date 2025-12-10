package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.TestSessionConfig;
import bruzsal.dnsmanagement.controller.request.DDnsCommand;
import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.controller.request.IpAddressType;
import bruzsal.dnsmanagement.dto.DnsRecordDto;
import bruzsal.dnsmanagement.service.alt.IpAddress;
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

import static bruzsal.dnsmanagement.controller.request.IpAddressType.IPV4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSessionConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DnsRecordControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    static final String TYPE = "TXT";
    static final String NAME = "testit.dnsrecordcontrollerit" + new Random().nextLong();
    static final String CONTENT = "\"test createDnsRecords\"";

    String dnsRecordId;

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
        assertNotNull(dnsRecordDto);
        dnsRecordId = dnsRecordDto.id();
    }

    @AfterAll
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
    void getAllRecordTest() {
        webTestClient
                .get()
                .uri("/api/records")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DnsRecordDto.class);
    }

    @Test
    @Disabled("Warning DELETE all dns record which start with `test`")
    void deleteAllTestDnsRecordsExceptOne() {
        List<DnsRecordDto> list = webTestClient
                .get()
                .uri("/api/records/filterByNameStartsWith?prefix=test")
                .exchange()
                .expectBodyList(DnsRecordDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(list).isNotEmpty();
        list.forEach(dnsRecord -> {
            if (!dnsRecordId.equals(dnsRecord.id())) {
                webTestClient
                        .delete()
                        .uri("/api/records/{id}", dnsRecord.id())
                        .exchange()
                        .expectStatus().isOk();
            }
        });
    }

    @Test
    void updateDns(@Autowired IpAddress ipAddress) {
        IpAddressType ipAddressType = IPV4;
        String name = "test.ddns.laci.lol";
        String ip = ipAddress.get(ipAddressType);
        DDnsCommand dDnsCommand = new DDnsCommand(ipAddressType, name, ip);

        DnsRecordDto dnsRecordDto = webTestClient
                .patch()
                .uri("/api/records/ddns")
                .bodyValue(dDnsCommand)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(DnsRecordDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(dnsRecordDto);
        assertEquals(ipAddressType.value, dnsRecordDto.type());
        assertEquals(name, dnsRecordDto.name());
        assertEquals(ip, dnsRecordDto.content());
    }

    @Test
    void getAllDnsRecordsNameStartsWith() {
        String testPrefix = "testIT";
        webTestClient
                .get()
                .uri("/api/records/filterByNameStartsWith?prefix={testPrefix}", testPrefix)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DnsRecordDto.class)
                .hasSize(1)
                .value(dnsRecordDtos ->
                {
                    assertNotNull(dnsRecordDtos);
                    DnsRecordDto dnsRecord = dnsRecordDtos.getFirst();
                    assertNotNull(dnsRecord);
                    assertEquals(NAME + "." + zoneName, dnsRecord.name());
                });
    }


}