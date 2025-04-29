package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.ApiResponse;
import com.mehedi.prismweather.dto.auth.*;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.factory.ResponseFactory;
import com.mehedi.prismweather.model.Profile;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import com.mehedi.prismweather.service.auth.PasswordAuthenticationStrategy;
import com.mehedi.prismweather.service.auth.TokenAuthenticationStrategy;
import com.mehedi.prismweather.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private static final String EMAIL_ALREADY_TAKEN = "Email is already taken";
    private static final String EMAIL_NOT_FOUND = "Email not found";
    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    private final ResponseFactory responseFactory;
    private final PasswordAuthenticationStrategy passwordAuthStrategy;
    private final TokenAuthenticationStrategy tokenAuthStrategy;

    public AuthService(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder, 
            JwtUtil jwtUtil,
            JwtBlacklistService jwtBlacklistService,
            ResponseFactory responseFactory,
            PasswordAuthenticationStrategy passwordAuthStrategy,
            TokenAuthenticationStrategy tokenAuthStrategy) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtBlacklistService = jwtBlacklistService;
        this.responseFactory = responseFactory;
        this.passwordAuthStrategy = passwordAuthStrategy;
        this.tokenAuthStrategy = tokenAuthStrategy;
    }

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        // Use the password authentication strategy
        User user = passwordAuthStrategy.authenticate(loginRequest);

        String token = jwtUtil.generateToken(user);

        LoginResponse.UserData userData = LoginResponse.UserData.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .user(userData)
                .build();

        return responseFactory.success("Login successful", loginResponse);
    }

    public ApiResponse<RegisterResponse> register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Registration attempt failed: email {} is already taken", registerRequest.getEmail());
            throw new CustomException(EMAIL_ALREADY_TAKEN, 409);
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Profile profile = Profile.builder()
                .country(registerRequest.getCountry())
                .countryCode(registerRequest.getCountryCode())
                .state(registerRequest.getState())
                .address(registerRequest.getAddress())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .password(hashedPassword)
                .username(registerRequest.getName())
                .accountType(registerRequest.getAccountType())
                .profile(profile)
                .role(User.Role.USER) // Default role is USER
                .build();

        User savedUser = userRepository.save(newUser);

        String token = jwtUtil.generateToken(savedUser);

        RegisterResponse registerResponse = RegisterResponse.builder()
                .token(token)
                .user(RegisterResponse.UserData.builder()
                        .email(newUser.getEmail())
                        .name(newUser.getUsername())
                        .accountType(newUser.getAccountType())
                        .country(profile.getCountry())
                        .countryCode(profile.getCountryCode())
                        .state(profile.getState())
                        .address(profile.getAddress())
                        .phoneNumber(profile.getPhoneNumber())
                        .build())
                .build();

        return responseFactory.created("User registered successfully", registerResponse);
    }

    public ApiResponse<PasswordResetResponse> resetPassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(() -> new CustomException(EMAIL_NOT_FOUND, 404));

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
        return responseFactory.success("Password reset successfully", passwordResetResponse);
    }

    public ApiResponse<LogoutResponse> logout(String token) {
        // Use the token authentication strategy to validate the token
        User user = tokenAuthStrategy.authenticate(token);

        // Get claims for expiration time
        Claims claims = jwtUtil.validateTokenAndGetClaims(token);
        long expirationTimeInMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        long expirationInSeconds = expirationTimeInMillis / 1000;

        // Blacklist the token
        jwtBlacklistService.blacklistToken(token, expirationInSeconds);

        LogoutResponse logoutResponse = LogoutResponse.builder()
                .email(user.getEmail())
                .build();

        log.info("User {} logged out successfully", user.getUsername());

        return responseFactory.success("User logged out successfully", logoutResponse);
    }
}
