package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.LoginRequest;
import com.mehedi.prismweather.dto.request.PasswordResetRequest;
import com.mehedi.prismweather.dto.request.RegisterRequest;
import com.mehedi.prismweather.dto.response.*;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import com.mehedi.prismweather.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password", 400));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password", 400);
        }

        String token = jwtUtil.generateToken(user);

        LoginResponse.UserData userData = LoginResponse.UserData.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .user(userData)
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                loginResponse
        );
    }

    public ApiResponse<RegisterResponse> register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Registration attempt failed: email {} is already taken", registerRequest.getEmail());
            throw new CustomException("Email is already taken", 409);
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(hashedPassword)
                .username(registerRequest.getName())
                .accountType(registerRequest.getAccountType())
                .country(registerRequest.getCountry())
                .countryCode(registerRequest.getCountryCode())
                .state(registerRequest.getState())
                .address(registerRequest.getAddress())
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(User.Role.USER) // Default role is USER
                .build();

        User savedUser = userRepository.save(newUser);

        String token = jwtUtil.generateToken(savedUser);

        RegisterResponse registerResponse = RegisterResponse.builder()
                .token(token)
                .user(RegisterResponse.UserData.builder()
                        .email(savedUser.getEmail())
                        .name(savedUser.getUsername())
                        .accountType(savedUser.getAccountType())
                        .country(savedUser.getCountry())
                        .countryCode(savedUser.getCountryCode())
                        .state(savedUser.getState())
                        .address(savedUser.getAddress())
                        .phoneNumber(savedUser.getPhoneNumber())
                        .build())
                .build();

        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                registerResponse
        );
    }

    /**
     * Reset Password Logic
     *
     * @param passwordResetRequest Contains the email and new password
     * @return A response containing confirmation and user information
     */
    public ApiResponse<PasswordResetResponse> resetPassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new CustomException("Email not found", 404));

        String hashedPassword = passwordEncoder.encode(passwordResetRequest.getPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);

        PasswordResetResponse.UserData userData = PasswordResetResponse.UserData.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .build();

        PasswordResetResponse passwordResetResponse = PasswordResetResponse.builder()
                .message("User password reset successfully")
                .user(userData)
                .build();

        log.info("Password reset successfully for email: {}", passwordResetRequest.getEmail());
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Password reset successfully",
                passwordResetResponse
        );
    }

    /**
     * Log out a user by invalidating their JWT token.
     *
     * @param token The user's current JWT token
     * @return A logout response containing the user information
     */
    public ApiResponse<LogoutResponse> logout(String token) {
        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        if (claims == null) {
            throw new CustomException("Invalid or expired token", 400);
        }

        String userEmail = claims.getSubject();
        String userName = claims.get("name", String.class);

        // Check if the user exists in the database
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", 404));

        long expirationTimeInMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        long expirationInSeconds = expirationTimeInMillis / 1000;

        // Blacklist the token in Redis
        jwtBlacklistService.blacklistToken(token, expirationInSeconds);

        LogoutResponse logoutResponse = LogoutResponse.builder()
                .email(userEmail)
                .build();

        log.info("User {} logged out successfully", userName);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User logged out successfully",
                logoutResponse
        );
    }
}
