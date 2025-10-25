package bruzsal.dnsmanagement.dto;


import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;

public record CloudflareErrorDto(
        Object result,
        boolean success,
        ArrayList<Error> errors,
        ArrayList<String> messages

) {

    public record Messages(
            int code,
            String message
    ) {
    }

    public record Error(
            int code,
            String message,

            @JsonAlias({"error_chain"})
            ArrayList<Messages> messages
    ) {
    }

}
