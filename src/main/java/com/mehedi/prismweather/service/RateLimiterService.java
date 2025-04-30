package com.mehedi.prismweather.service;

import com.mehedi.prismweather.exception.CustomException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    private static final String RATE_LIMITER_KEY_PREFIX = "rate_limiter:";
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SECONDS = 10;

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimiterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Check if the request is allowed based on the rate limit.
     * If the request is allowed, increment the counter.
     * If the request is not allowed, throw a CustomException.
     *
     * @param key The key to identify the client (e.g., IP address, user ID)
     * @throws CustomException if the rate limit is exceeded
     */
    public void checkRateLimit(String key) {
        String redisKey = RATE_LIMITER_KEY_PREFIX + key;
        
        // Get the current count
        Long count = redisTemplate.opsForValue().increment(redisKey, 1);
        
        // If this is the first request, set the expiration time
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        // If the count exceeds the limit, throw an exception
        if (count != null && count > MAX_REQUESTS) {
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            String message = String.format("Rate limit exceeded. Try again in %d seconds.", ttl != null ? ttl : WINDOW_SECONDS);
            throw new CustomException(message, HttpStatus.TOO_MANY_REQUESTS.value());
        }
    }
}