package com.mehedi.prismweather.util;

import com.mehedi.prismweather.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Generates a JWT token for the given user.
     *
     * @param user The authenticated user
     * @return JWT token
     */
    public String generateToken(User user) {
        // Decode the Base64-encoded secret key
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name()) // Optional custom claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
