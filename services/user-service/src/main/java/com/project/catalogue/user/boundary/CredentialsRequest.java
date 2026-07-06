package com.project.catalogue.user.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CredentialsRequest(
        @Email @NotBlank @Schema(example = "john@mail.com") String email,
        @NotBlank @Schema(example = "secret123") String password
) {}
