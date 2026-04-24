package io.yerektus.qadam.coreapi.modules.auth;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.*;
import io.yerektus.qadam.coreapi.modules.auth.service.AuthService;
import io.yerektus.qadam.coreapi.modules.auth.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Mono<RegisterResponse> register(@Valid @RequestBody RegisterBody body) {
        return authService.register(body);
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Mono<UserDto> me() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UUID.class)
                .flatMap(authService::getCurrentUser);
    }
}
