package io.yerektus.qadam.coreapi.modules.document.controller;

import io.yerektus.qadam.coreapi.modules.document.dto.DocumentDto;
import io.yerektus.qadam.coreapi.modules.document.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<DocumentDto> upload(
            @PathVariable UUID projectId,
            @RequestPart("file") FilePart file,
            @RequestPart(value = "description", required = false) String description
    ) {
        return currentUserId()
                .flatMap(userId -> documentService.upload(userId, projectId, file, description));
    }

    @GetMapping
    public Flux<DocumentDto> list(@PathVariable UUID projectId) {
        return currentUserId()
                .flatMapMany(userId -> documentService.getDocuments(userId, projectId));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable UUID projectId, @PathVariable UUID id) {
        return currentUserId()
                .flatMap(userId -> documentService.deleteDocument(userId, projectId, id));
    }

    private Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UUID.class);
    }
}
