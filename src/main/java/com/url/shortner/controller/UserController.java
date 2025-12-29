package com.url.shortner.controller;

import com.url.shortner.dto.request.LoginRequest;
import com.url.shortner.dto.common.ApiResponse;
import com.url.shortner.dto.request.RegisterRequest;
import com.url.shortner.models.User;
import com.url.shortner.security.jwt.JwtAuthenticationResponse;
import com.url.shortner.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtAuthenticationResponse authenticationResponse = userService.authenticateUser(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(true, authenticationResponse, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request){
        User existingUser = userService.findByUsername(request.getUsername());
        if(existingUser == null) {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRole("USER_ROLE");
            userService.registerUser(user);
            return ResponseEntity.ok().body(new ApiResponse<>(true, null, "User registered successfully"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "User with username: "+request.getUsername()+" already exists"));
        }
    }
}
