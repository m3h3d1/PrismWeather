package com.mehedi.prismweather.service;

import com.mehedi.prismweather.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RateLimiterServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        rateLimiterService = new RateLimiterService(redisTemplate);
    }

    @Test
    void checkRateLimit_FirstRequest_ShouldSetExpiration() {
        String key = "test_key";
        when(valueOperations.increment("rate_limiter:test_key", 1L)).thenReturn(1L);

        rateLimiterService.checkRateLimit(key);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment("rate_limiter:test_key", 1);
        verify(redisTemplate).expire(eq("rate_limiter:test_key"), eq(10L), eq(TimeUnit.SECONDS));
    }

    @Test
    void checkRateLimit_WithinLimit_ShouldNotThrowException() {
        String key = "test_key";
        when(valueOperations.increment("rate_limiter:test_key", 1L)).thenReturn(3L);

        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(key));
        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment("rate_limiter:test_key", 1);
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void checkRateLimit_ExceedsLimit_ShouldThrowException() {
        String key = "test_key";
        when(valueOperations.increment("rate_limiter:test_key", 1L)).thenReturn(4L);
        when(redisTemplate.getExpire("rate_limiter:test_key", TimeUnit.SECONDS)).thenReturn(5L);

        CustomException exception = assertThrows(CustomException.class, () -> rateLimiterService.checkRateLimit(key));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), exception.getStatus());
        assertTrue(exception.getMessage().contains("Rate limit exceeded"));
        assertTrue(exception.getMessage().contains("5 seconds"));

        verify(redisTemplate).opsForValue();
        verify(valueOperations).increment("rate_limiter:test_key", 1);
        verify(redisTemplate).getExpire("rate_limiter:test_key", TimeUnit.SECONDS);
    }

    @Test
    void checkRateLimit_RedisConnectionFailure_ShouldNotThrowException() {
        String key = "test_key";
        when(redisTemplate.opsForValue()).thenThrow(new RedisConnectionFailureException("Connection refused"));

        // Should not throw an exception when Redis is unavailable
        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(key));

        // Verify that we attempted to access Redis
        verify(redisTemplate).opsForValue();
    }
}
