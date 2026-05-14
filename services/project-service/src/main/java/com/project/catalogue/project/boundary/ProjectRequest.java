package com.project.catalogue.project.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank @Size(max = 120) @Schema(example = "catalogue-api") String name,
        @NotBlank @Size(max = 500) @Schema(example = "https://github.com/user/catalogue-api") String location
) {}
