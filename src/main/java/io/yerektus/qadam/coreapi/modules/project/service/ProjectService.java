package io.yerektus.qadam.coreapi.modules.project.service;

import io.yerektus.qadam.coreapi.modules.project.dto.CreateProjectRequest;
import io.yerektus.qadam.coreapi.modules.project.dto.ProjectEditorDto;
import io.yerektus.qadam.coreapi.modules.project.dto.ProjectDto;
import io.yerektus.qadam.coreapi.modules.project.dto.UpdateProjectEditorRequest;
import io.yerektus.qadam.coreapi.modules.project.entity.Project;
import io.yerektus.qadam.coreapi.modules.project.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Mono<ProjectDto> create(UUID userId, CreateProjectRequest request) {
        Project project = new Project();
        project.setUserId(userId);
        project.setName(request.name().trim());
        project.setCreatedAt(LocalDateTime.now());

        return projectRepository.save(project)
                .map(this::toDto);
    }

    public Flux<ProjectDto> list(UUID userId) {
        return projectRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .map(this::toDto);
    }

    public Mono<ProjectEditorDto> getEditor(UUID userId, UUID projectId) {
        return requireProject(userId, projectId)
                .map(this::toEditorDto);
    }

    public Mono<ProjectEditorDto> updateEditor(
            UUID userId,
            UUID projectId,
            UpdateProjectEditorRequest request
    ) {
        return requireProject(userId, projectId)
                .flatMap(project -> {
                    project.setEditorContent(request.content());
                    return projectRepository.save(project);
                })
                .map(this::toEditorDto);
    }

    private ProjectDto toDto(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getUserId(),
                project.getName(),
                project.getCreatedAt()
        );
    }

    private ProjectEditorDto toEditorDto(Project project) {
        return new ProjectEditorDto(
                project.getId(),
                project.getEditorContent()
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
