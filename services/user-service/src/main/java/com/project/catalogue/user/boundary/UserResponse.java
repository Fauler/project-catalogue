package com.project.catalogue.user.boundary;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "john@mail.com") String email,
        @Schema(example = "John") String firstName,
        @Schema(example = "Doe") String lastName,
        LocalDateTime createdAt
) {}
