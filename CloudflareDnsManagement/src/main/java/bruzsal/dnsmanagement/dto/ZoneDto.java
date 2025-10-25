package bruzsal.dnsmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public record ZoneDto(
        String id,
        String name,
        String status,
        Boolean paused,
        String type,
        @JsonProperty("development_mode") Integer developmentMode,
        @JsonProperty("name_servers") List<String> nameServers,
        @JsonProperty("original_name_servers") List<String> originalNameServers,
        @JsonProperty("original_registrar") Object originalRegistrar,
        @JsonProperty("original_dnshost") Object originalDnsHost,
        @JsonProperty("modified_on") Date modifiedOn,
        @JsonProperty("created_on") Date createdOn,
        @JsonProperty("activated_on") Date activatedOn,
        Meta meta,
        Owner owner,
        Account account,
        Tenant tenant,
        @JsonProperty("tenant_unit") TenantUnit tenantUnit,
        List<String> permissions,
        Plan plan
) {

    public record Tenant(
            Object id,
            Object name
    ) {
    }

    public record TenantUnit(
            Object id
    ) {
    }

    public record Account(
            String id,
            String name
    ) {
    }

    public record Meta(
            Integer step,
            @JsonProperty("custom_certificate_quota") Integer customCertificateQuota,
            @JsonProperty("page_rule_quota") Integer pageRuleQuota,
            @JsonProperty("phishing_detected") Boolean phishingDetected,
            @JsonProperty("multiple_railguns_allowed") Boolean multipleRailgunsAllowed
    ) {
    }

    public record Owner(
            Object id,
            String type,
            Object email
    ) {
    }

    public record Plan(
            String id,
            String name,
            Integer price,
            String currency,
            String frequency,
            @JsonProperty("is_subscribed") Boolean isSubscribed,
            @JsonProperty("can_subscribe") Boolean canSubscribe,
            @JsonProperty("legacy_id") String legacyId,
            @JsonProperty("legacy_discount") Boolean legacyDiscount,
            @JsonProperty("externally_managed") Boolean externallyManaged
    ) {
    }
}