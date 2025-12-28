package com.url.shortner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank(message = "username should not be blank")
    private String username;

    @NotBlank(message = "email should not be blank")
    private String email;

    private Set<String> roles;

    @NotBlank(message = "password should not be blank")
    @Size(min = 8, message = "password should be of at least 8 characters")
    private String password;
}
