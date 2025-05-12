package com.mehedi.prismweather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JwtBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private JwtBlacklistService jwtBlacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        jwtBlacklistService = new JwtBlacklistService(redisTemplate);
    }

    @Test
    void blacklistToken_Success() {
        String token = "test-token";
        long expiration = 3600;

        jwtBlacklistService.blacklistToken(token, expiration);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq("blacklist:test-token"), eq("blacklisted"), eq(expiration), eq(TimeUnit.SECONDS));
    }

    @Test
    void blacklistToken_RedisConnectionFailure_ShouldNotThrowException() {
        String token = "test-token";
        long expiration = 3600;

        when(redisTemplate.opsForValue()).thenThrow(new RedisConnectionFailureException("Connection refused"));

        // Should not throw an exception when Redis is unavailable
        assertDoesNotThrow(() -> jwtBlacklistService.blacklistToken(token, expiration));

        // Verify that we attempted to access Redis
        verify(redisTemplate).opsForValue();
    }

    @Test
    void isTokenBlacklisted_TokenExists_ShouldReturnTrue() {
        String token = "test-token";
        when(redisTemplate.hasKey("blacklist:test-token")).thenReturn(true);

        boolean result = jwtBlacklistService.isTokenBlacklisted(token);

        assertTrue(result);
        verify(redisTemplate).hasKey("blacklist:test-token");
    }

    @Test
    void isTokenBlacklisted_TokenDoesNotExist_ShouldReturnFalse() {
        String token = "test-token";
        when(redisTemplate.hasKey("blacklist:test-token")).thenReturn(false);

        boolean result = jwtBlacklistService.isTokenBlacklisted(token);

        assertFalse(result);
        verify(redisTemplate).hasKey("blacklist:test-token");
    }

    @Test
    void isTokenBlacklisted_RedisConnectionFailure_ShouldReturnFalse() {
        String token = "test-token";
        when(redisTemplate.hasKey("blacklist:test-token")).thenThrow(new RedisConnectionFailureException("Connection refused"));

        boolean result = jwtBlacklistService.isTokenBlacklisted(token);

        // Should return false when Redis is unavailable
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklist:test-token");
    }
}