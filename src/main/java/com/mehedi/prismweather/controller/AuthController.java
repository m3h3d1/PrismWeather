package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.ApiResponse;
import com.mehedi.prismweather.dto.auth.*;
import com.mehedi.prismweather.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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

    /**
     * Reset Password Endpoint: Allows users to reset their password.
     */
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        ApiResponse<PasswordResetResponse> response = authService.resetPassword(passwordResetRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout Endpoint: Invalidate the user's JWT token.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        ApiResponse<LogoutResponse> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts JWT token from the Authorization header.
     * 
     * @param request The HTTP request
     * @return The JWT token
     * @throws IllegalArgumentException if the Authorization header is missing or invalid
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header missing or invalid");
        }

        return authorizationHeader.substring(7);
    }
}
