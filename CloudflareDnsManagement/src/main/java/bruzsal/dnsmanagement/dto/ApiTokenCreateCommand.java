package bruzsal.dnsmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

@Value
public class ApiTokenCreateCommand implements Serializable {

    @NotNull(message = "Api token must not be null")
    String apiToken;

}