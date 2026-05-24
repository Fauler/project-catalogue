package com.project.catalogue.project.boundary;

import com.project.catalogue.project.domain.exception.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.model.Project;
import com.project.catalogue.project.domain.repository.ProjectRepository;
import com.project.catalogue.project.domain.UserValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository repository;
    private final UserValidator userValidator;
    private final Counter projectsAdded;
    private final Counter projectsRemoved;

    public ProjectService(ProjectRepository repository, UserValidator userValidator, MeterRegistry registry) {
        this.repository = repository;
        this.userValidator = userValidator;
        this.projectsAdded = Counter.builder("projects.added").description("Projects added").register(registry);
        this.projectsRemoved = Counter.builder("projects.removed").description("Projects removed").register(registry);
    }

    @Transactional
    public ProjectResponse addProjectToUser(Long userId, ProjectRequest request) {
        log.info("Adding project '{}' to user {}", request.location(), userId);
        userValidator.validateUserExists(userId);

        if (repository.findByUserIdAndLocation(userId, request.location()).isPresent()) {
            log.warn("Project already exists for user {} at location {}", userId, request.location());
            throw new ProjectAlreadyExistsException(request.location());
        }

        Project project = new Project(
                null,
                userId,
                request.name(),
                request.location(),
                null,
                null
        );

        Project saved = repository.save(project);
        projectsAdded.increment();
        log.debug("Project {} created with id {}", saved.getLocation(), saved.getId());
        return toResponse(saved);
    }

    public Page<ProjectResponse> listProjectsByUser(Long userId, int page, int size) {
        log.info("Listing projects for user {} - page {}, size {}", userId, page, size);
        userValidator.validateUserExists(userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return repository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public void removeProject(Long userId, Long projectId) {
        log.info("Removing project {} from user {}", projectId, userId);
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(String.valueOf(projectId)));

        if (!project.getUserId().equals(userId)) {
            throw new ProjectNotFoundException(String.valueOf(projectId));
        }

        repository.deleteById(projectId);
        projectsRemoved.increment();
        log.debug("Project {} deleted", projectId);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getUserId(),
                project.getName(),
                project.getLocation(),
                project.getCreatedAt()
        );
    }

}
