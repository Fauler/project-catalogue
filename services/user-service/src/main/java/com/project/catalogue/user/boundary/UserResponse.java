package com.project.catalogue.user.boundary;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDateTime createdAt
) {}
