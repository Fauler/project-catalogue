package com.project.catalogue.user.boundary;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Email @NotBlank String email,
        @Size(max = 120) String firstName,
        @Size(max = 120) String lastName,
        @NotBlank @Size(min = 8) String password
) {}

