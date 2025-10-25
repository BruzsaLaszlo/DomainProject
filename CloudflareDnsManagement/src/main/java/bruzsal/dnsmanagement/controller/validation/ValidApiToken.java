package bruzsal.dnsmanagement.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ApiTokenValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidApiToken {

    String message() default "Invalid Cloudflare api token";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
