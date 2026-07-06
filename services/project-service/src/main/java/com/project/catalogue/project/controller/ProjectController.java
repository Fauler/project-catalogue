package com.project.catalogue.project.controller;

import com.project.catalogue.project.boundary.ApiResult;
import com.project.catalogue.project.boundary.ProjectRequest;
import com.project.catalogue.project.boundary.ProjectResponse;
import com.project.catalogue.project.boundary.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RestController
@RequestMapping("/api/v1/users/{userId}/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Add a project to a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project added"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Project already exists for this user")
    })
    @PostMapping
    public ResponseEntity<ApiResult<ProjectResponse>> addProjectToUser(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody ProjectRequest request
    ) {
        ProjectResponse response = projectService.addProjectToUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success(HttpStatus.CREATED, response));
    }

    @Operation(summary = "List projects for a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects listed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<ProjectResponse>>> listProjectsByUser(
            @PathVariable @Positive Long userId,
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResult.success(HttpStatus.OK, projectService.listProjectsByUser(userId, pageable)));
    }

    @Operation(summary = "Remove a project from a user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Project removed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> removeProject(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long projectId
    ) {
        projectService.removeProject(userId, projectId);
        return ResponseEntity.noContent().build();
    }
}
