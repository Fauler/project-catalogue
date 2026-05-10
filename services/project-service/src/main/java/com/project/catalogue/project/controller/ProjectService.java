package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.domain.exception.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.model.Project;
import com.project.catalogue.project.domain.repository.ProjectRepository;
import com.project.catalogue.project.domain.repository.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final UserValidator userValidator;

    /**
     * Adds a project to a valid user
     */
    public ProjectResponse addProjectToUser(Long userId, ProjectRequest request) {
        log.info("Adding project '{}' to user {}", request.getLocation(), userId);
        userValidator.validateUserExists(userId);

        if (repository.findByUserIdAndLocation(userId, request.getLocation()).isPresent()) {
            log.warn("Project already exists for user {} at location {}", userId, request.getLocation());
            throw new ProjectAlreadyExistsException(request.getLocation());
        }

        Project project = new Project(
                null,
                userId,
                request.getName(),
                request.getLocation(),
                null,
                null
        );

        Project saved = repository.save(project);
        log.debug("Project {} created with id {}", saved.getLocation(), saved.getId());
        return toResponse(saved);
    }

    public Page<ProjectResponse> listProjectsByUser(Long userId, int page, int size) {
        log.debug("Listing projects for user {} — page {}, size {}", userId, page, size);
        userValidator.validateUserExists(userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return repository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    public void removeProject(Long userId, Long projectId) {
        log.info("Removing project {} from user {}", projectId, userId);
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(String.valueOf(projectId)));

        if (!project.getUserId().equals(userId)) {
            throw new ProjectNotFoundException(String.valueOf(projectId));
        }

        repository.deleteById(projectId);
        log.debug("Project {} deleted", projectId);
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .userId(project.getUserId())
                .name(project.getName())
                .location(project.getLocation())
                .createdAt(project.getCreatedAt())
                .build();
    }

}
