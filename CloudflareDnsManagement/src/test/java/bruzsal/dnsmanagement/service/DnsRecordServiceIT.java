package bruzsal.dnsmanagement.service;

import bruzsal.dnsmanagement.TestSessionConfig;
import bruzsal.dnsmanagement.controller.request.DDnsCommand;
import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.dto.DnsRecordDto;
import bruzsal.dnsmanagement.exception.DnsRecordAmbiguousException;
import bruzsal.dnsmanagement.exception.DnsRecordException;
import bruzsal.dnsmanagement.exception.DnsRecordNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static bruzsal.dnsmanagement.controller.request.IpAddressType.IPV4;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSessionConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DnsRecordServiceIT {

    @Autowired
    DnsRecordService dnsRecordService;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    String dnsType;
    String dnsName;
    String dnsId;
    DnsRecordCommand dnsRecordCommand;

    @BeforeAll
    void create() {
        dnsName = "test.dnsrecordserviceit." + zoneName;
        dnsType = "A";
        dnsRecordCommand = new DnsRecordCommand(dnsType, dnsName, "127.0.0.1");

        List<DnsRecordDto> list = dnsRecordService.getDnsRecordBy(of(dnsType), of(dnsName), empty());
        if (list.isEmpty()) {
            dnsId = createDNSRecord().id();
        } else {
            dnsId = list.getFirst().id();
        }
    }

    private DnsRecordDto createDNSRecord() {
        DnsRecordDto dnsRecordDto = dnsRecordService.createDnsRecord(dnsRecordCommand);
        assertThat(dnsRecordDto)
                .returns(dnsType, DnsRecordDto::type)
                .returns(dnsName, DnsRecordDto::name)
                .returns(dnsRecordCommand.getContent(), DnsRecordDto::content);
        return dnsRecordDto;
    }

    @AfterAll
    void deleteDNSRecord() {
        assertEquals(dnsId, dnsRecordService.deleteDnsRecord(dnsId));
    }

    @Test
    @Order(1)
    void testCreateExist() {
        assertThatExceptionOfType(DnsRecordException.class)
                .isThrownBy(() -> dnsRecordService.createDnsRecord(dnsRecordCommand));
    }

    @Test
    void getAllRecordTest() {
        List<DnsRecordDto> allRecords = dnsRecordService.getAllRecords();
        assertThat(allRecords)
                .isNotEmpty()
                .extracting(DnsRecordDto::type)
                .anyMatch(dnsType::equals);
    }

    @Test
    void getRecordDetailsTest() {
        DnsRecordDto dnsRecordDto = dnsRecordService.getDnsRecordById(dnsId);
        assertEquals(dnsId, dnsRecordDto.id());

        assertThatExceptionOfType(DnsRecordNotFoundException.class)
                .isThrownBy(() -> dnsRecordService.getDnsRecordById("invalid id"))
                .withMessageContaining("invalid id");
    }

    @Test
    void updateDnsRecordByNameTest() {
        dnsRecordCommand.setContent("10.11.10.11");

        DnsRecordDto dnsRecordDto = dnsRecordService.updateDnsRecord(dnsRecordCommand);

        assertThat(dnsRecordDto)
                .extracting(DnsRecordDto::type, DnsRecordDto::name, DnsRecordDto::content)
                .containsExactly(dnsType, dnsName, dnsRecordCommand.getContent());
    }

    @Test
    void getFilteredRecordsTest() {
        Optional<String> type = ofNullable(dnsType);
        Optional<String> name = ofNullable(dnsName);
        DnsRecordDto test = dnsRecordService.getDnsRecordBy(type, name, empty()).getFirst();
        assertThat(type)
                .isPresent()
                .contains(test.type());
        assertThat(name)
                .isPresent()
                .contains(test.name());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Ddns {
        String ddnsId;

        @Test
        void updateDnsIpAddress() {
            DDnsCommand dDnsCommand = new DDnsCommand(IPV4, "test.ipv4.ddns." + zoneName, "127.0.0.1");
            DnsRecordDto dnsRecordDto = dnsRecordService.updateDnsIpAddress(dDnsCommand);
            ddnsId = dnsRecordDto.id();
            assertEquals(dDnsCommand.getIpAddress(), dnsRecordDto.content());
        }

        @AfterAll
        void afterAll() {
            assertEquals(ddnsId, dnsRecordService.deleteDnsRecord(ddnsId));
        }
    }

    @Test
    void getDnsSingleRecordBy() {
        Optional<String> type = of("A");
        Optional<String> empty = empty();
        assertThatThrownBy(() -> dnsRecordService.getDnsSingleRecordBy(type, empty, empty))
                .isInstanceOf(DnsRecordAmbiguousException.class);
    }
}