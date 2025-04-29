package com.mehedi.prismweather.service.auth;

import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import com.mehedi.prismweather.service.JwtBlacklistService;
import com.mehedi.prismweather.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

/**
 * Token-based authentication strategy.
 * Implements the Strategy pattern for authenticating users with JWT tokens.
 */
@Component
public class TokenAuthenticationStrategy implements AuthenticationStrategy {
    
    private static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String TOKEN_BLACKLISTED = "Token has been invalidated";
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtBlacklistService jwtBlacklistService;
    
    public TokenAuthenticationStrategy(
            UserRepository userRepository, 
            JwtUtil jwtUtil,
            JwtBlacklistService jwtBlacklistService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.jwtBlacklistService = jwtBlacklistService;
    }
    
    @Override
    public User authenticate(Object credentials) {
        if (!(credentials instanceof String)) {
            throw new IllegalArgumentException("Credentials must be of type String (token)");
        }
        
        String token = (String) credentials;
        
        // Check if token is blacklisted
        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            throw new CustomException(TOKEN_BLACKLISTED, 401);
        }
        
        Claims claims = jwtUtil.validateTokenAndGetClaims(token);
        
        if (claims == null) {
            throw new CustomException(INVALID_OR_EXPIRED_TOKEN, 401);
        }
        
        String userEmail = claims.getSubject();
        
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND, 404));
    }
}