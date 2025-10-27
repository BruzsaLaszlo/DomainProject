package bruzsal.dnsmanagement.service.alt;

import bruzsal.dnsmanagement.controller.request.IpAddressType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IpAddress {

    private final RestClient restClient = RestClient.create();

    public String get(IpAddressType ipAddressType) {
        return restClient
                .get()
                .uri("https://api{num}.ipify.org", ipAddressType.number)
                .retrieve()
                .body(String.class);
    }


}
