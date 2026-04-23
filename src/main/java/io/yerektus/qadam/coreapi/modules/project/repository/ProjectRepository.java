package io.yerektus.qadam.coreapi.modules.project.repository;

import io.yerektus.qadam.coreapi.modules.project.entity.Project;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProjectRepository extends ReactiveCrudRepository<Project, UUID> {
    Flux<Project> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    Mono<Project> findByIdAndUserId(UUID id, UUID userId);
}
