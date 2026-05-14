package com.project.catalogue.project.boundary;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ProjectResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long userId,
        @Schema(example = "catalogue-api") String name,
        @Schema(example = "https://github.com/user/catalogue-api") String location,
        LocalDateTime createdAt
) {}
