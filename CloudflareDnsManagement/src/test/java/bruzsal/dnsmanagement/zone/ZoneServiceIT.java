package bruzsal.dnsmanagement.zone;

import bruzsal.dnsmanagement.TestSessionConfig;
import bruzsal.dnsmanagement.dto.ZoneDto;
import bruzsal.dnsmanagement.exception.ZoneNotFoundException;
import bruzsal.dnsmanagement.service.ZoneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@Import(TestSessionConfig.class)
class ZoneServiceIT {

    @Autowired
    ZoneService zoneService;

    @Value("${cloudflare.api-token}")
    String apiToken;

    @Value("${cloudflare.zone-id}")
    String zoneId;

    @Value("${cloudflare.zone-name}")
    String zoneName;

    @Test
    void getZoneByNameTest() {
        ZoneDto zoneDto = zoneService.getZoneByName(zoneName);
        assertEquals(zoneId, zoneDto.id());

        assertThatThrownBy(() -> zoneService.getZoneByName("invalid name"))
                .isInstanceOf(ZoneNotFoundException.class);
    }

    @Test
    void getZoneById() {
        ZoneDto zone = zoneService.getZoneById(zoneId);
        assertEquals(zoneId, zone.id());
    }

}