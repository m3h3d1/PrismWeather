package com.mehedi.prismweather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(JwtBlacklistService.class);
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    private final RedisTemplate<String, Object> redisTemplate;

    public JwtBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Blacklist a JWT token by storing it in Redis with an expiration time.
     *
     * @param token       The JWT token to blacklist
     * @param expiration  The TTL (time-to-live) in seconds
     */
    public void blacklistToken(String token, long expiration) {
        String key = BLACKLIST_KEY_PREFIX + token;
        try {
            redisTemplate.opsForValue().set(key, "blacklisted", expiration, TimeUnit.SECONDS);
        } catch (org.springframework.data.redis.RedisConnectionFailureException e) {
            logger.warn("Failed to blacklist token: Redis connection error. Application will continue without blacklisting.", e);
        }
    }

    /**
     * Check if a token is blacklisted.
     *
     * @param token The JWT token to check
     * @return True if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (org.springframework.data.redis.RedisConnectionFailureException e) {
            logger.warn("Failed to check if token is blacklisted: Redis connection error. Assuming token is not blacklisted.", e);
            return false;
        }
    }
}
