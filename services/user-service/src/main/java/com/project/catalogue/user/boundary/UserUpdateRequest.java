package com.project.catalogue.user.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Email @NotBlank @Schema(example = "john@mail.com") String email,
        @Size(max = 120) @Schema(example = "John") String firstName,
        @Size(max = 120) @Schema(example = "Doe") String lastName
) {}
