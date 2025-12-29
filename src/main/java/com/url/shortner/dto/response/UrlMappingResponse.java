package com.url.shortner.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlMappingResponse {
    private String originalUrl;
    private String shortUrl;
    private int clickCount;
    private LocalDateTime createdDate;
    private String username;
    private Long id;
}
