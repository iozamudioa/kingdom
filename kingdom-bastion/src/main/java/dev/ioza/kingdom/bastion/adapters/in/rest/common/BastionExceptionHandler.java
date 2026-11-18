package dev.ioza.kingdom.bastion.adapters.in.rest.common;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import dev.ioza.kingdom.bastion.domain.model.realm.InvalidRealmCredentialsException;
import dev.ioza.kingdom.bastion.domain.model.realm.RealmUnavailableException;

@RestControllerAdvice
public class BastionExceptionHandler {
        @ExceptionHandler(InvalidRealmCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentials(
                        InvalidRealmCredentialsException exception, ServerWebExchange exchange) {
                ErrorResponse response = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                                "invalid_client", "Invalid client credentials",
                                exchange.getRequest().getURI().toString());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        @ExceptionHandler(RealmUnavailableException.class)
        public ResponseEntity<ErrorResponse> handleRealmUnavailable(
                        RealmUnavailableException exception, ServerWebExchange exchange) {
                ErrorResponse response = ErrorResponse.of(HttpStatus.SERVICE_UNAVAILABLE.value(),
                                "realm_unavailable", "Identity realm is temporarily unavailable",
                                exchange.getRequest().getURI().toString());

                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        @ExceptionHandler(WebExchangeBindException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException exception, ServerWebExchange exchange) {
                Map<String, String> details = new LinkedHashMap<>();

                exception.getBindingResult().getFieldErrors().forEach(error -> details
                                .putIfAbsent(error.getField(), error.getDefaultMessage()));

                ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                                "invalid_request", "Request validation failed",
                                exchange.getRequest().getURI().toString(), details);

                return ResponseEntity.badRequest().body(response);
        }
}
