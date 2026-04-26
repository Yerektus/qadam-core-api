package io.yerektus.qadam.coreapi.modules.auth.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.yerektus.qadam.coreapi.modules.auth.model.entity.AccessToken;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccessTokenRepository extends ReactiveCrudRepository<AccessToken, UUID> {
    Mono<AccessToken> findByToken(String token);
    Mono<Boolean> existsByToken(String token);
    Mono<Void> deleteByToken(String token);
    Mono<Void> deleteAllByUserId(UUID userId);
}
