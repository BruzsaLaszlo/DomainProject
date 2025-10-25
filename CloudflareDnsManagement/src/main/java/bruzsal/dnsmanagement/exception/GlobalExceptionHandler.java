package bruzsal.dnsmanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;

/**
 * A paraméter lehet:
 * HttpServletRequest and HttpServletResponse
 * WebRequest
 * Principal and Authentication
 * HttpSession
 * Locale
 * Model
 * <p>
 * The commonly used return types are:
 * ResponseEntity
 * ModelAndView
 * String (View Name)
 * ResponseBody
 * Void (writing the response content directly to HttpServletResponse)
 */

//@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ProblemDetail handleHttpClientErrorException(HttpClientErrorException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatusCode());
        pd.setTitle("HTTP Client Error");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("https://" + request.getServerName() + request.getContextPath()));
        return pd;
    }

    @ExceptionHandler(ResponseException.class)
    public ProblemDetail handleResponseException(ResponseException re, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(BAD_REQUEST);
        pd.setTitle("Response Error from Cloudflare API");
        pd.setDetail(re.getMessage());
        pd.setType(URI.create("https://" + request.getServerName() + request.getContextPath()));
        return pd;
    }


    @ExceptionHandler(AuthenticationException.class)
    protected ProblemDetail handleAuthenticationException(AuthenticationException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String exResponse = Arrays.toString(ex.getResponse().getBody().readAllBytes());
        logger.error("""
                ex.getResponse().getBody().readAllBytes(): %s
                HttpServletResponse response.getStatus(): %d
                ex.getResponse().getStatusCode(): %d
                HttpServletRequest request.getRequestURI(): %s
                ex.getRequest().getURI().toString(): %s
                %s"""
                .formatted(
                        exResponse,
                        response.getStatus(),
                        ex.getResponse().getStatusCode().value(),
                        request.getRequestURI(),
                        ex.getRequest().getURI().toString(),
                        response.getHeaderNames().toString()
                ));
        ProblemDetail pd = ProblemDetail.forStatus(ex.getResponse().getStatusCode());
        pd.setTitle("Authentication Error");
        pd.setDetail("Cloudflare API Token is invalid: " + ex.getRequest().getHeaders().get("Authorization"));
        pd.setType(URI.create("https://%s/api/error/authentication".formatted(request.getServerName())));
        return pd;
    }

    @ExceptionHandler(DnsRecordNotFoundException.class)
    protected ProblemDetail handleZoneNotFoundException(HttpServletRequest request, DnsRecordNotFoundException drnfe) {
        ProblemDetail pd = ProblemDetail.forStatus(NOT_FOUND);
        pd.setTitle("DNS Record Not Found");
        pd.setDetail(drnfe.getMessage());
        pd.setType(URI.create("https://" + request.getServerName() + request.getContextPath()));
        return pd;
    }

    @ExceptionHandler(ZoneNotFoundException.class)
    protected ProblemDetail handleZoneNotFoundException(HttpServletRequest request, ZoneNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(NOT_FOUND);
        pd.setTitle("Zone Not Found");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("https://" + request.getServerName() + request.getContextPath()));
        return pd;
    }

    @ExceptionHandler(DnsRecordAmbiguousException.class)
    protected ProblemDetail handleDnsRecordAmbiguousException(DnsRecordAmbiguousException exception, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(NOT_ACCEPTABLE);
        pd.setTitle("DNS Record Ambiguous, More than one dns record found!");
        pd.setDetail(exception.getMessage());
        pd.setType(URI.create("https://" + request.getServerName() + request.getContextPath()));
        return pd;
    }

    @ExceptionHandler(DnsRecordException.class)
    protected ProblemDetail handleZoneNotFoundException(HttpServletRequest request, DnsRecordException dre) {
        ProblemDetail pd = ProblemDetail.forStatus(BAD_REQUEST);
        pd.setTitle(dre.getTitle());
        pd.setDetail(dre.getDetail());
        pd.setType(URI.create("https://%s/api/error".formatted(request.getServerName())));
        return pd;
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage internalServerError(final Exception ex) {
        String message;
        try {
            message = messageSource.getMessage("exception.INTERNAL_SERVER_ERROR", null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException _) {
            message = "";
        }
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value() + message);
    }

    @ExceptionHandler(ApiTokenNotFoundException.class)
    public ProblemDetail handleApiTokenNotFoundException(HttpServletRequest request, ApiTokenNotFoundException ex) {
        logger.warn("API token missing for request: " + request.getRequestURI());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("API Token Not Found");
        pd.setDetail(ex.getMessage() != null ? ex.getMessage() : "Required API token was not provided.");
        pd.setType(URI.create("https://" + request.getServerName() + "/api/error/api-token-not-found"));
        return pd;
    }

    @ExceptionHandler(ApiTokenInvalidException.class)
    public ProblemDetail handleApiTokenNotFoundException(HttpServletRequest request, ApiTokenInvalidException ex) {
        logger.warn("API token is invalid: " + request.getRequestURI());
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("API Token is invalid");
        pd.setDetail(ex.getMessage() != null ? ex.getMessage() : "Required API token is invalid.");
        pd.setType(URI.create("https://" + request.getServerName() + "/api/error/api-token-is-invalid"));
        return pd;
    }
}
