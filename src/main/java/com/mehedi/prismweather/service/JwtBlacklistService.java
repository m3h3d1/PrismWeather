package com.mehedi.prismweather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Blacklist a JWT token by storing it in Redis with an expiration time.
     *
     * @param token       The JWT token to blacklist
     * @param expiration  The TTL (time-to-live) in seconds
     */
    public void blacklistToken(String token, long expiration) {
        String key = BLACKLIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expiration, TimeUnit.SECONDS);
    }

    /**
     * Check if a token is blacklisted.
     *
     * @param token The JWT token to check
     * @return True if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
