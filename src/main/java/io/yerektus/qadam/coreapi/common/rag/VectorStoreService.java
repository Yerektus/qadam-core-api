package io.yerektus.qadam.coreapi.common.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final DatabaseClient databaseClient;

    public VectorStoreService(VectorStore vectorStore, DatabaseClient databaseClient) {
        this.vectorStore = vectorStore;
        this.databaseClient = databaseClient;
    }

    /**
     * Adds documents to the vector store (blocking call wrapped in Mono).
     */
    public Mono<Void> addDocuments(List<Document> documents) {
        return Mono.fromRunnable(() -> vectorStore.add(documents))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    /**
     * Searches the vector store filtering by userId.
     */
    public Mono<List<Document>> search(String query, UUID userId) {
        return Mono.fromCallable(() -> {
            FilterExpressionBuilder b = new FilterExpressionBuilder();
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .filterExpression(b.eq("userId", userId.toString()).build())
                    .build();
            return vectorStore.similaritySearch(request);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Deletes all vectors associated with a document from the vector_store table.
     * Uses native SQL via R2DBC since PgVectorStore doesn't support metadata-based deletion.
     */
    public Mono<Void> deleteByDocumentId(UUID documentId) {
        return databaseClient.sql("DELETE FROM vector_store WHERE metadata->>'documentId' = :documentId")
                .bind("documentId", documentId.toString())
                .then();
    }
}
