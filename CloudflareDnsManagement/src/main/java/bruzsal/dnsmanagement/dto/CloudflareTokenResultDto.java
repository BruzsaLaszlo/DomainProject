package bruzsal.dnsmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;

public record CloudflareTokenResultDto(
        Result result,
        boolean success,
        ArrayList<String> errors,
        ArrayList<Message> messages) {


    public record Message(
            int code,
            String message,
            Object type) {
    }

    public record Result(
            String id,
            String status,
            @JsonProperty("not_before")
            LocalDate notBefore,
            @JsonProperty("expires_on")
            LocalDate expiresOn) {
    }
}
