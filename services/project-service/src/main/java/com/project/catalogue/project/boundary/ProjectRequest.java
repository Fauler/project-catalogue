package com.project.catalogue.project.boundary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String location;
}
