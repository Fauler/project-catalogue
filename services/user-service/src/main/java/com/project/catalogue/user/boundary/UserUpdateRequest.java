package com.project.catalogue.user.boundary;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Email @NotBlank String email,
        @Size(max = 120) String firstName,
        @Size(max = 120) String lastName
) {}

