package bruzsal.acmeclient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewCertificateCommand {

    /**
     * for testing: "https://acme-staging-v02.api.letsencrypt.org/directory";
     */
    @NotBlank
    private String serverUrl = "https://acme-v02.api.letsencrypt.org/directory";
    @NotBlank
    private String apiKey;
    @NotBlank
    private String zoneId;

    @Email
    private String emailAddress;

    private String accountKey;
    private String certificatoinRequest;
    private String distinguishedName;

    @NotBlank
    private String domainName;

    public NewCertificateCommand(String apiKey, String zoneId, String emailAddress, String distinguishedName, String domainName) {
        this.apiKey = apiKey;
        this.zoneId = zoneId;
        this.emailAddress = emailAddress;
        this.distinguishedName = distinguishedName;
        this.domainName = domainName;
    }
}
