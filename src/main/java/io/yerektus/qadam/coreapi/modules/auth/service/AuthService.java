package io.yerektus.qadam.coreapi.modules.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.yerektus.qadam.coreapi.modules.auth.model.dto.AccessTokenDto;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.AuthResponse;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.LoginBody;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.RegisterBody;
import io.yerektus.qadam.coreapi.modules.auth.model.dto.UserResponse;
import reactor.core.publisher.Mono;

@Service
public interface AuthService {
    Mono<AuthResponse> register(RegisterBody payload);
    Mono<AuthResponse> login(LoginBody request);
    Mono<UserResponse> getCurrentUser(UUID userId);
    Mono<AccessTokenDto> getAccessTokenByToken(String token);
    Mono<Boolean> existsAccessTokenByToken(String token);
    Mono<Void> deleteByToken(String token);
}
