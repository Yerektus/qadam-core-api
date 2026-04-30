package io.yerektus.qadam.coreapi.modules.document.service;

import io.yerektus.qadam.coreapi.common.rag.VectorStoreService;
import io.yerektus.qadam.coreapi.common.storage.LocalStorageService;
import io.yerektus.qadam.coreapi.modules.document.dto.DocumentDto;
import io.yerektus.qadam.coreapi.modules.document.entity.Document;
import io.yerektus.qadam.coreapi.modules.document.repository.DocumentRepository;
import io.yerektus.qadam.coreapi.modules.project.entity.Project;
import io.yerektus.qadam.coreapi.modules.project.repository.ProjectRepository;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final LocalStorageService localStorageService;
    private final VectorStoreService vectorStoreService;
    private final DocumentProcessingService documentProcessingService;
    private final ProjectRepository projectRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            LocalStorageService localStorageService,
            VectorStoreService vectorStoreService,
            DocumentProcessingService documentProcessingService,
            ProjectRepository projectRepository
    ) {
        this.documentRepository = documentRepository;
        this.localStorageService = localStorageService;
        this.vectorStoreService = vectorStoreService;
        this.documentProcessingService = documentProcessingService;
        this.projectRepository = projectRepository;
    }

    /**
     * Uploads a document: saves metadata, stores file, and triggers async processing.
     */
    public Mono<DocumentDto> upload(UUID userId, UUID projectId, FilePart filePart, String description) {
        String fileName = filePart.filename();

        return requireProject(userId, projectId)
                .flatMap(project -> {
                    Document doc = new Document();
                    doc.setUserId(userId);
                    doc.setProjectId(project.getId());
                    doc.setFileName(fileName);
                    doc.setFilePath("");
                    doc.setDescription(description);
                    doc.setStatus("PROCESSING");
                    doc.setCreatedAt(LocalDateTime.now());
                    doc.setUpdatedAt(LocalDateTime.now());

                    return documentRepository.save(doc);
                })
                .flatMap(saved -> {
                        UUID documentId = saved.getId();
                        if (documentId == null) {
                            return Mono.error(new IllegalStateException("Document ID was not generated"));
                        }

                        // Join all data buffers and convert to byte array
                        return DataBufferUtils.join(filePart.content())
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    return bytes;
                                })
                                .flatMap(bytes -> localStorageService.store(userId, documentId, fileName, bytes))
                                .flatMap(filePath -> {
                                    saved.setFilePath(filePath);
                                    saved.setUpdatedAt(LocalDateTime.now());
                                    return documentRepository.save(saved);
                                });
                })
                .doOnSuccess(saved -> {
                    // Fire-and-forget async processing
                    documentProcessingService.process(saved.getId());
                })
                .map(this::toDto);
    }

    /**
     * Returns all documents for the given user.
     */
    public Flux<DocumentDto> getDocuments(UUID userId, UUID projectId) {
        return requireProject(userId, projectId)
                .flatMapMany(project -> documentRepository.findAllByUserIdAndProjectId(userId, project.getId()))
                .map(this::toDto);
    }

    /**
     * Deletes a document: removes file, vectors, and DB record.
     */
    public Mono<Void> deleteDocument(UUID userId, UUID projectId, UUID documentId) {
        return requireProject(userId, projectId)
                .flatMap(project -> documentRepository.findByIdAndUserIdAndProjectId(documentId, userId, project.getId()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Document not found: " + documentId
                )))
                .flatMap(doc -> localStorageService.delete(doc.getFilePath())
                        .then(vectorStoreService.deleteByDocumentId(documentId))
                        .then(documentRepository.deleteById(documentId)));
    }

    private DocumentDto toDto(Document doc) {
        return new DocumentDto(
                doc.getId(),
                doc.getUserId(),
                doc.getProjectId(),
                doc.getFileName(),
                doc.getDescription(),
                doc.getStatus(),
                doc.getCreatedAt()
        );
    }

    private Mono<Project> requireProject(UUID userId, UUID projectId) {
        return projectRepository.findByIdAndUserId(projectId, userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found: " + projectId
                )));
    }
}
