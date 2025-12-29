package com.url.shortner.utils;

import com.url.shortner.dto.common.ApiError;
import com.url.shortner.dto.common.ApiResponse;
import com.url.shortner.utils.exceptions.BadRequestException;
import com.url.shortner.utils.exceptions.ForbiddenException;
import com.url.shortner.utils.exceptions.ResourceNotFoundException;
import com.url.shortner.utils.exceptions.UnauthorizedException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse>globalExceptionHandler(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, null, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, null, ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, null, ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(ForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(false, null, ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> globalExceptionHandler(RuntimeException ex) {
        log.error("Error while executing the request: {}",ex.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, null, Constants.INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidArgument(MethodArgumentNotValidException ex) {
        List<ApiError> errorList = new ArrayList<>();

        ex.getBindingResult()
           .getFieldErrors()
                .forEach(error -> errorList.add(new ApiError(error.getField(), error.getDefaultMessage())));
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, errorList, "Validation failed"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(false, null, ex.getMessage()));
    }
}
