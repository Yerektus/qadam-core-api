package io.yerektus.qadam.coreapi.common.exception;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(
            ResponseStatusException ex,
            ServerWebExchange exchange
    ) {
        HttpStatusCode status = ex.getStatusCode();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                ex.getReason()
        );

        problem.setTitle(HttpStatus.valueOf(status.value()).getReasonPhrase());
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        problem.setProperty("requestId", exchange.getRequest().getId());

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            WebExchangeBindException ex
    ) {
        Map<String, String> errors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (first, second) -> first
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", "Validation failed",
                        "errors", errors
                ));
    }
}