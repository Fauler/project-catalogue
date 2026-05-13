package com.project.catalogue.project.boundary;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        Long userId,
        String name,
        String location,
        LocalDateTime createdAt
) {}
