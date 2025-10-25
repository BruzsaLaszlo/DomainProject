package bruzsal.dnsmanagement.controller.request;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IpAddressType {
    IPV4("A", 4),
    IPV6("AAAA", 6);
    public final String value;
    public final int number;
}
