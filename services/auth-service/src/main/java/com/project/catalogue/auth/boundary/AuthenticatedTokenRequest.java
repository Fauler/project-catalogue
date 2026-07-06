package com.project.catalogue.auth.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticatedTokenRequest(
        @NotBlank @Schema(example = "client-admin-01") String clientId,
        @Email @NotBlank @Schema(example = "john@mail.com") String email,
        @NotBlank @Schema(example = "secret123") String password
) {}
