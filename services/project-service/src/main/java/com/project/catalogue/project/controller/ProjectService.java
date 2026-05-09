package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.domain.ExternalProject;
import com.project.catalogue.project.domain.ProjectAlreadyExistsException;
import com.project.catalogue.project.domain.ProjectNotFoundException;
import com.project.catalogue.project.domain.ProjectRepository;
import com.project.catalogue.project.domain.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

        if (repository.existsById(request.getId())) {
            throw new ProjectAlreadyExistsException(request.getId());
        }

        ExternalProject project = new ExternalProject(
                request.getId(),
                userId,
                request.getName(),
                null,
                null
        );

        ExternalProject saved = repository.save(project);
        return toResponse(saved);
    }

    public List<ProjectResponse> listProjectsByUser(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void removeProject(Long userId, String projectId) {
        ExternalProject project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!project.getUserId().equals(userId)) {
            throw new ProjectNotFoundException(projectId);
        }

        repository.deleteById(projectId);
    }

    private ProjectResponse toResponse(ExternalProject project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .userId(project.getUserId())
                .name(project.getName())
                .createdAt(project.getCreatedAt())
                .build();
    }

}

