package bruzsal.dnsmanagement.controller.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DnsRecordCommand {

    private String type;
    private String name;
    private String content;

    /**
     * Comments or notes about the DNS record. This field has no effect on DNS responses.
     */
    private String comment = "";

    /**
     * Whether the record is receiving the performance and security benefits of Cloudflare.
     */
    private boolean proxied = false;

    /**
     * When enabled, only A records will be generated, and AAAA records will not be created.
     * This setting is intended for exceptional cases. Note that this option only applies to proxied records and
     * it has no effect on whether Cloudflare communicates with the origin using IPv4 or IPv6.
     */
    private String settings = null;

    /**
     * Custom tags for the DNS record. This field has no effect on DNS responses.
     */
    private List<String> tags;

    /**
     * Time To Live (TTL) of the DNS record in seconds. Setting to 1 means 'automatic'.
     * Value must be between 60 and 86400, with the minimum reduced to 30 for Enterprise zones.
     */
    private int ttl = 1;


    public DnsRecordCommand(String type, String name, String content) {
        this.type = type;
        this.name = name;
        this.content = content;
    }

}

enum Settings {IPV4_ONLY, IPV6_ONLY}
