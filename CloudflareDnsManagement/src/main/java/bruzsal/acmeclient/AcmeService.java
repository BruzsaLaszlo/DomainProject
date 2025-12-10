package bruzsal.acmeclient;

import bruzsal.dnsmanagement.controller.request.DnsRecordCommand;
import bruzsal.dnsmanagement.dto.CertificatesDto;
import bruzsal.dnsmanagement.dto.DnsRecordDto;
import bruzsal.dnsmanagement.service.DnsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

import static java.nio.file.Files.readString;
import static java.nio.file.Path.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcmeService {

    private final DnsRecordService dnsRecordService;
    private final KeyGenerator keyGenerator;

    private final String baseDir = "certificates/";

    public Boolean generateCertificate() throws NoSuchAlgorithmException {
        //TODO
        throw new NoSuchAlgorithmException();
    }

    public Boolean revokeCertification(RevokeCerficationCommand revokeCerficationCommand) throws NoSuchAlgorithmException {
        //TODO
        throw new NoSuchAlgorithmException();
    }

    public CertificatesDto createCertificate(NewCertificateCommand newCertificateCommand) {
//        ClientTest.run(List.of("ssl5.laci.lol"));
        throw new NoSuchElementException();
    }

    private String validateCert(String certificateRequest, String distinguishedName) {
        if (certificateRequest == null || certificateRequest.isBlank()) {
            KeyPair domainKeyPair = keyGenerator.generateKeyPair(baseDir + "domain.key", 4096);
            keyGenerator.generateCSR(domainKeyPair, baseDir + "domain.csr", distinguishedName/*, CERT_ALT_NAME*/);
            return baseDir + "domain.csr";
        }
        return certificateRequest;
    }

    private String validateAccountKey(String accountKey) {
        if (accountKey == null) {
            keyGenerator.generateKeyPair(baseDir + "account.key", 4096);
            return baseDir + "account.key";
        }
        return accountKey;
    }

    private CertificatesDto readCerts(String dir) {
        CertificatesDto certificatesDto = new CertificatesDto();
        try {
            certificatesDto.setCert(readString(of(dir + "/certs/cert.pem")));
            certificatesDto.setChain(readString(of(dir + "/certs/chain.pem")));
            certificatesDto.setFullChain(readString(of(dir + "/certs/fullchain.pem")));
            certificatesDto.setAccountKey(readString(of(baseDir + "account.key")));
            certificatesDto.setDomainKey(readString(of(baseDir + "domainKey.pem")));
//            deleteDirectory(of(dir).toFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read certificate files from: " + dir, e);
        }
        return certificatesDto;
    }

    private String executeChallenge(String domainName, String digestDir) {
        String digest = getDigest(domainName, digestDir);
        DnsRecordDto dnsRecordDto = dnsRecordService.createDnsRecord(new DnsRecordCommand("TXT", "_acme-challenge." + domainName, digest));
        // Wait 10 seconds for DNS propagation.
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return dnsRecordDto.id();
    }

    private static String getDigest(String domainName, String digestDir) {
        try {
            Path digestFile = of(digestDir + "/" + domainName + "_dns_digest");
            return readString(digestFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read digest file!", e);
        }
    }


    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        try {
            Files.delete(directoryToBeDeleted.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete directory!", e);
        }
    }
}
