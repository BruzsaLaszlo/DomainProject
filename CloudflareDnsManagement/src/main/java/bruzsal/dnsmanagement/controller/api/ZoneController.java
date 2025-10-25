package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.session.UserSession;
import bruzsal.dnsmanagement.dto.ZoneDto;
import bruzsal.dnsmanagement.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@Tag(name = "Operations on Zone")
public class ZoneController {

    private final ZoneService zoneService;
    private final UserSession userSession;

    @GetMapping("/{zoneName}")
    public ZoneDto getZoneByName(@PathVariable String zoneName) {
        return zoneService.getZoneByName(zoneName);
    }

    @GetMapping("/actual")
    @Operation(summary = "Az zóna lekérdezése")
    @ApiResponse(
            responseCode = "200",
            description = "A zóna id",
            content = @Content(mediaType = "application/json"))
    public ZoneDto getActualZone() {
        return zoneService.getZoneById(userSession.getZoneId());
    }

    @PostMapping
    public ResponseEntity<ZoneDto> addZone(@RequestParam String zoneName) {
        ZoneDto zone = zoneService.getZoneByName(zoneName);
        userSession.setZoneId(zone.id());
        return ResponseEntity.ok(zone);
    }

    @GetMapping
    public List<ZoneDto> getAllZone() {
        return zoneService.getAllZone();
    }

}
