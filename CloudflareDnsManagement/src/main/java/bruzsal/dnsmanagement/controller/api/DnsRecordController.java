package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.request.DDnsCommand;
import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.dto.model.DnsRecordDto;
import bruzsal.dnsmanagement.service.DnsRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "Operations on Zone")
public class DnsRecordController {

    private final DnsRecordService dnsRecordService;

    @GetMapping
    public List<DnsRecordDto> getAllRecords(HttpSession session) {
        return dnsRecordService.getAllRecords();
    }

    @GetMapping("/{recordId}")
    public DnsRecordDto getDnsRecord(@PathVariable String recordId) {
        return dnsRecordService.getDnsRecordDetailsById(recordId);
    }

    @GetMapping("/filter")
    public List<DnsRecordDto> getAllDnsRecordsFilteredBy(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> content
    ) {
        return dnsRecordService.getDnsRecordBy(type, name, content);
    }

    @GetMapping("/filterByNameStartsWith")
    public List<DnsRecordDto> getAllDnsRecordsNameStartsWith(@RequestParam Optional<String> prefix
    ) {
        return dnsRecordService.getDnsRecordNameStartsWith(prefix);
    }

    @PatchMapping("/findAndUpdate")
    @ResponseStatus(ACCEPTED)
    public DnsRecordDto updateDnsRecord(@RequestBody DnsRecordCommand dnsRecordCommand) {
        return dnsRecordService.updateDnsRecord(dnsRecordCommand);
    }

    @PatchMapping("/ddns")
    @ResponseStatus(ACCEPTED)
    public DnsRecordDto dDns(@RequestBody DDnsCommand dDnsCommand) {
        return dnsRecordService.updateDnsIpAddress(dDnsCommand);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public DnsRecordDto newDnsRecord(@RequestBody DnsRecordCommand dnsRecordCommand) {
        return dnsRecordService.createDnsRecord(dnsRecordCommand);
    }

    @DeleteMapping("/{recordId}")
    public String deleteDnsRecord(@PathVariable String recordId) {
        return dnsRecordService.deleteDnsRecord(recordId);
    }

}
