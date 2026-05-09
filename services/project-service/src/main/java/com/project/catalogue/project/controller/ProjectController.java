package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> addProjectToUser(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.addProjectToUser(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjectsByUser(@PathVariable @Positive Long userId) {
        return ResponseEntity.ok(projectService.listProjectsByUser(userId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> removeProject(
            @PathVariable @Positive Long userId,
            @PathVariable @NotBlank String projectId
    ) {
        projectService.removeProject(userId, projectId);
        return ResponseEntity.noContent().build();
    }
}

