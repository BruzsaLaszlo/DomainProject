package bruzsal.acmeclient;

import bruzsal.dnsmanagement.dto.CertificatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
public class AcmeController {

    private final AcmeService acmeService;

    @PostMapping
    public CertificatesDto createCertificateForDomain(@RequestBody NewCertificateCommand newCertificateCommand) {
        return acmeService.createCertificate(newCertificateCommand);
    }

}
