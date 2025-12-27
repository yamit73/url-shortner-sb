package com.url.shortner.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private List<ApiError> errors;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public ApiResponse(boolean success, T data, List<ApiError> errors, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errors = errors;
    }
}
