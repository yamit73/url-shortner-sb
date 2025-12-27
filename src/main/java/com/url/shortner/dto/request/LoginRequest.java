package com.url.shortner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message= "userName is required")
    private String userName;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password should be of at least 8 characters")
    private String password;
}
