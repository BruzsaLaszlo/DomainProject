package bruzsal.acmeclient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Path;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcmeServiceTestIT {

    @Autowired
    AcmeService acmeService;

    @Value("${cloudflare.api-token}")
    String apiToken;

    @Value("${cloudflare.zone-id}")
    String zoneId;

    @TempDir
    Path tempDir;

    @Test
    void createCertificate() {
        String domainName = "test.ssl.laci.lol";
        NewCertificateCommand newCertificateCommand = new NewCertificateCommand(
                apiToken,
                zoneId,
                "lacika007@gmail.com",
                "CN=%s,O=Organisation,OU=Organisational Unit,L=Budapest,ST=Budapest,C=HU,EMAIL=lacika007@gmail.com".formatted(domainName),
                domainName
        );


        assertThatThrownBy(() -> {
            acmeService.createCertificate(newCertificateCommand);
        }).isInstanceOf(NoSuchElementException.class);

//        Files.writeString(of("s/cert.crt"),certificate.getFullChain());
//        Files.writeString(of("s/domain.key"),certificate.getDomainKey());
//        Files.writeString(of("s/account.key"),certificate.getAccountKey());
    }

}