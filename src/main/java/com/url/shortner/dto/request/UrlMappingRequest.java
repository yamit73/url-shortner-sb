package com.url.shortner.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UrlMappingRequest {
    @NotBlank(message = "originalUrl field should not be blank")
    private String originalUrl;
}
