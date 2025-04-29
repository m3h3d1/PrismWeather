package com.mehedi.prismweather.service.auth;

import com.mehedi.prismweather.model.User;

/**
 * Strategy interface for authentication methods.
 * Implements the Strategy pattern to allow different authentication methods to be used interchangeably.
 */
public interface AuthenticationStrategy {
    
    /**
     * Authenticates a user based on the provided credentials.
     * 
     * @param credentials The credentials to authenticate with
     * @return The authenticated user if successful
     * @throws com.mehedi.prismweather.exception.CustomException if authentication fails
     */
    User authenticate(Object credentials);
}