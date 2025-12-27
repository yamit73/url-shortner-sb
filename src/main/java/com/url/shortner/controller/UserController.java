package com.url.shortner.controller;

import com.url.shortner.dto.request.LoginRequest;
import com.url.shortner.dto.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("Request body: {}", req.toString());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ApiResponse(true, null, "Login successful"));
    }
}
