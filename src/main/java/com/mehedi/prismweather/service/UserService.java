package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.response.UserProfileResponse;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the current logged-in user's profile.
     *
     * @param email The email of the logged-in user.
     * @return A DTO containing the user's profile information.
     */
    public UserProfileResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .name(user.getUsername())
                .email(user.getEmail())
                .address(UserProfileResponse.UserAddress.builder()
                        .country(user.getCountry())
                        .state(user.getState())
                        .build())
                .build();
    }
}
