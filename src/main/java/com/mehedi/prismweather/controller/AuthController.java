package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.request.LoginRequest;
import com.mehedi.prismweather.dto.request.RegisterRequest;
import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.LoginResponse;
import com.mehedi.prismweather.dto.response.RegisterResponse;
import com.mehedi.prismweather.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * Login Endpoint: Authenticate user and return a JWT token with user details.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Register Endpoint: Allows users to create an account.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<RegisterResponse> apiResponse = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
