package io.yerektus.qadam.coreapi.modules.project.controller;

import io.yerektus.qadam.coreapi.modules.project.dto.CreateProjectRequest;
import io.yerektus.qadam.coreapi.modules.project.dto.ProjectEditorDto;
import io.yerektus.qadam.coreapi.modules.project.dto.ProjectDto;
import io.yerektus.qadam.coreapi.modules.project.dto.UpdateProjectEditorRequest;
import io.yerektus.qadam.coreapi.modules.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public Mono<ProjectDto> create(@Valid @RequestBody CreateProjectRequest request) {
        return currentUserId()
                .flatMap(userId -> projectService.create(userId, request));
    }

    @GetMapping
    public Flux<ProjectDto> list() {
        return currentUserId()
                .flatMapMany(projectService::list);
    }

    @GetMapping("/{projectId}/editor")
    public Mono<ProjectEditorDto> getEditor(@PathVariable UUID projectId) {
        return currentUserId()
                .flatMap(userId -> projectService.getEditor(userId, projectId));
    }

    @PutMapping("/{projectId}/editor")
    public Mono<ProjectEditorDto> updateEditor(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectEditorRequest request
    ) {
        return currentUserId()
                .flatMap(userId -> projectService.updateEditor(userId, projectId, request));
    }

    private Mono<UUID> currentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UUID.class);
    }
}
