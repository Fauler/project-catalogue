package com.project.catalogue.project.boundary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 500) String location
) {}

