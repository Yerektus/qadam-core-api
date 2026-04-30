package io.yerektus.qadam.coreapi.modules.leads;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.yerektus.qadam.coreapi.modules.leads.model.entity.Lead;
import reactor.core.publisher.Mono;

public interface LeadsRepository extends ReactiveCrudRepository<Lead, UUID> {
    Mono<Boolean> existsByEmail(String email);
}
