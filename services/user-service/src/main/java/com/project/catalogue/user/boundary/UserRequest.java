package com.project.catalogue.user.boundary;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @Email
    @NotBlank
    private String email;

    @Size(max = 120)
    private String firstName;

    @Size(max = 120)
    private String lastName;

    @NotBlank
    @Size(min = 8)
    private String password;
}
