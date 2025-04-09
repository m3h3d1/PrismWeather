package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.LoginRequest;
import com.mehedi.prismweather.dto.request.PasswordResetRequest;
import com.mehedi.prismweather.dto.request.RegisterRequest;
import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.LoginResponse;
import com.mehedi.prismweather.dto.response.PasswordResetResponse;
import com.mehedi.prismweather.dto.response.RegisterResponse;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import com.mehedi.prismweather.util.JwtUtil;
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

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        // Fetch user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException("Invalid email or password", 400));

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password", 400);
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(user);

        // Build the response
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
        // Check if the email is already in use
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Registration attempt failed: email {} is already taken", registerRequest.getEmail());
            throw new CustomException("Email is already taken", 409);
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create a new User object
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

        // Save the user to the database
        User savedUser = userRepository.save(newUser);

        // Generate a JWT Token for the newly registered user
        String token = jwtUtil.generateToken(savedUser);

        // Build the response
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

        // Wrap response with ApiResponse class
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
}
