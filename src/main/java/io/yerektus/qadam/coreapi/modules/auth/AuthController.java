package io.yerektus.qadam.coreapi.modules.auth;

import io.yerektus.qadam.coreapi.common.util.ValueMapper;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.*;
import io.yerektus.qadam.coreapi.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterBody body) {
        log.info("AuthController::register request body={}", ValueMapper.jsonAsString(body));

        return authService.register(body)
                .doOnNext(response -> log.info("AuthController::register reponse=", ValueMapper.jsonAsString(body)));
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginBody body) {
        log.info("AuthController::login request body={}", ValueMapper.jsonAsString(body));

        return authService.login(body)
                .doOnNext(response -> log.info("AuthController::login response={}", ValueMapper.jsonAsString(response)));
    }

    @GetMapping("/me")
    public Mono<UserResponse> me() {
        log.info("AuthController::me request");

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UUID.class)
                .flatMap(authService::getCurrentUser)
                .doOnNext(response -> log.info("AuthController::me response={}", ValueMapper.jsonAsString(response)));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        boolean hasBearerToken = authHeader != null && authHeader.startsWith("Bearer ");
        log.info("AuthController::logout request bearerTokenPresent={}", hasBearerToken);

        if (!hasBearerToken) {
            log.warn("AuthController::logout rejected: missing bearer token");
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing bearer token"
            ));
        }

        String token = authHeader.substring(7);

        return authService.deleteByToken(token)
                .doOnSuccess(unused -> log.info("AuthController::logout response status={}", HttpStatus.NO_CONTENT.value()));
    }
}
