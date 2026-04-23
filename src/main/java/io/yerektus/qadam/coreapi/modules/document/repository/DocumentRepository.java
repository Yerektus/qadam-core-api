package io.yerektus.qadam.coreapi.modules.document.repository;

import io.yerektus.qadam.coreapi.modules.document.entity.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface DocumentRepository extends ReactiveCrudRepository<Document, UUID> {
    Flux<Document> findAllByUserIdAndProjectId(UUID userId, UUID projectId);
    Mono<Document> findByIdAndUserIdAndProjectId(UUID id, UUID userId, UUID projectId);
}
