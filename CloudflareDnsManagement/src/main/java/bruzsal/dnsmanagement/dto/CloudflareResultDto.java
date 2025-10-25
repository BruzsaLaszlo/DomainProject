package bruzsal.dnsmanagement.dto;

import lombok.NonNull;

import java.util.ArrayList;

public record CloudflareResultDto<T>(
        @NonNull
        T result,
        @NonNull
        boolean success,
        ArrayList<String> errors,
        ArrayList<String> messages
) {

    @Override
    public String toString() {
        return "result=" + result + '\n' +
                ", success=" + success + '\n' +
                ", errors=" + errors + '\n' +
                ", messages=" + messages + '\n' +
                '}';
    }
}
