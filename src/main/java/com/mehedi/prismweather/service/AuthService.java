package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.ApiResponse;
import com.mehedi.prismweather.dto.auth.*;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.Profile;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import com.mehedi.prismweather.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    private static final String EMAIL_ALREADY_TAKEN = "Email is already taken";
    private static final String EMAIL_NOT_FOUND = "Email not found";
    private static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";
    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            JwtBlacklistService jwtBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_EMAIL_OR_PASSWORD, 400));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_EMAIL_OR_PASSWORD, 400);
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
                loginResponse);
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

        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "User registered successfully",
                registerResponse);
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
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Password reset successfully",
                passwordResetResponse);
    }

    public ApiResponse<LogoutResponse> logout(String token) {
        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        if (claims == null) {
            throw new CustomException(INVALID_OR_EXPIRED_TOKEN, 400);
        }

        String userEmail = claims.getSubject();
        String userName = claims.get("name", String.class);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, 404));

        long expirationTimeInMillis = claims.getExpiration().getTime() - System.currentTimeMillis();
        long expirationInSeconds = expirationTimeInMillis / 1000;

        jwtBlacklistService.blacklistToken(token, expirationInSeconds);

        LogoutResponse logoutResponse = LogoutResponse.builder()
                .email(userEmail)
                .build();

        log.info("User {} logged out successfully", userName);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User logged out successfully",
                logoutResponse);
    }
}
