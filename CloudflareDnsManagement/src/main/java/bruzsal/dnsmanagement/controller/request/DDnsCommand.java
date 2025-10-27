package bruzsal.dnsmanagement.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DDnsCommand {

    @NotEmpty
    private IpAddressType type;

    @NotEmpty
    private String domain;

    @NotEmpty
    private String ipAddress;

}
