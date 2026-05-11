package com.project.catalogue.auth.boundary;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank String clientId
) {}
