package bruzsal.dnsmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertificatesDto {

    private String cert;
    private String chain;
    private String fullChain;

    private String accountKey;
    private String domainKey;

}
