package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.domain.exception.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.exception.ProjectNotFoundException;
import com.project.catalogue.project.domain.model.Project;
import com.project.catalogue.project.domain.repository.ProjectRepository;
import com.project.catalogue.project.domain.repository.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final UserValidator userValidator;

    /**
     * Adds a project to a valid user
     */
    public ProjectResponse addProjectToUser(Long userId, ProjectRequest request) {
        userValidator.validateUserExists(userId);

        if (repository.findByUserIdAndLocation(userId, request.getLocation()).isPresent()) {
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

        return toResponse(repository.save(project));
    }

    public Page<ProjectResponse> listProjectsByUser(Long userId, int page, int size) {
        userValidator.validateUserExists(userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return repository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    public void removeProject(Long userId, Long projectId) {
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(String.valueOf(projectId)));

        if (!project.getUserId().equals(userId)) {
            throw new ProjectNotFoundException(String.valueOf(projectId));
        }

        repository.deleteById(projectId);
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
