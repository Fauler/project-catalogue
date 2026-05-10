package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> addProjectToUser(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody ProjectRequest request
    ) {
        ProjectResponse response = projectService.addProjectToUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> listProjectsByUser(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int page,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, projectService.listProjectsByUser(userId, page, size)));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> removeProject(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long projectId
    ) {
        projectService.removeProject(userId, projectId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, null));
    }
}
