package io.yerektus.qadam.coreapi.modules.auth.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.yerektus.qadam.coreapi.modules.auth.model.entity.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByPhoneNumber(String phone);
}
