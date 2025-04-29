package com.mehedi.prismweather.service.auth;

import com.mehedi.prismweather.dto.auth.LoginRequest;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password-based authentication strategy.
 * Implements the Strategy pattern for authenticating users with email and password.
 */
@Component
public class PasswordAuthenticationStrategy implements AuthenticationStrategy {
    
    private static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public PasswordAuthenticationStrategy(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User authenticate(Object credentials) {
        if (!(credentials instanceof LoginRequest)) {
            throw new IllegalArgumentException("Credentials must be of type LoginRequest");
        }
        
        LoginRequest loginRequest = (LoginRequest) credentials;
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_EMAIL_OR_PASSWORD, 400));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_EMAIL_OR_PASSWORD, 400);
        }
        
        return user;
    }
}