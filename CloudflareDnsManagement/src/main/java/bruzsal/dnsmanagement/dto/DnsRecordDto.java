package bruzsal.dnsmanagement.dto;

import java.util.ArrayList;
import java.util.Date;

public record DnsRecordDto(
        String id,
        String zone_id,
        String zone_name,
        String name,
        String type,
        String content,
        Boolean proxiable,
        Boolean proxied,
        Integer ttl,
        Boolean locked,
        Meta meta,
        String comment,
        ArrayList<String> tags,
        Date created_on,
        Date modified_on
) {

    record Meta(
            Boolean auto_added,
            Boolean managed_by_apps,
            Boolean managed_by_argo_tunnel,
            String source
    ) {
    }


    @Override
    public String toString() {
        return '\n' + "id = " + id + '\n' +
                "   " + name + '\n' +
                "   " + type + '\n' +
                "   " + content + '\n' +
                "------" + '\n';
    }


}
