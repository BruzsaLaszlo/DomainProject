package bruzsal.dnsmanagement.record;

import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.dto.model.DnsRecordDto;
import bruzsal.dnsmanagement.service.DnsRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DDnsTest {

    @Autowired
    DnsRecordService dnsRecordService;

    @Value("${cloudflare.api-token}")
    String apiToken;

    @Value("${cloudflare.zone-id}")
    String zoneId;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    @Autowired
    private UserSession userSession;

    @Autowired
    HttpClient httpClient;

    @Test
    void updateDns() {
        userSession.setApiToken(apiToken);
        userSession.setZoneId(zoneId);

        String type = "A";
        String name = "test.ddns.laci.my";
        DnsRecordCommand dnsRecordCommand = new DnsRecordCommand(type, name, getMyIpAddress());
        List<DnsRecordDto> dnsRecordDtos = dnsRecordService.getDnsRecordBy(
                ofNullable(dnsRecordCommand.getType()),
                ofNullable(dnsRecordCommand.getName()),
                empty());

        DnsRecordDto dns;
        if (dnsRecordDtos.isEmpty()) {
            dns = dnsRecordService.createDnsRecord(dnsRecordCommand);
        } else {
            dns = dnsRecordService.updateDnsRecord(dnsRecordCommand);
        }

        assertEquals(type, dns.type());
        assertEquals(name, dns.name());
        assertEquals(dnsRecordCommand.getContent(), dns.content());
    }

    String getMyIpAddress() {
        try {
            return httpClient.send(
                    HttpRequest.newBuilder(URI.create("https://api.ipify.org")).build(),
                    HttpResponse.BodyHandlers.ofString()
            ).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Can't get ipv4 address", e);
        }
    }
}
