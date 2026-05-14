package com.project.catalogue.auth.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank @Schema(example = "client-admin-01") String clientId
) {}
