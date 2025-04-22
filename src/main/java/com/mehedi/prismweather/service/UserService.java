package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.UpdateUserRequest;
import com.mehedi.prismweather.dto.response.UserProfileResponse;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.Profile;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.ProfileRepository;
import com.mehedi.prismweather.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
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
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = profileRepository.findById(user.getId())
                    .orElseThrow(() -> new CustomException("Profile not found",
                            HttpStatus.NOT_FOUND.value()));
        }
        System.out.println(profile);

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .accountType(user.getAccountType())
                .profile(UserProfileResponse.UserProfile.builder()
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .country(profile.getCountry())
                        .countryCode(profile.getCountryCode())
                        .state(profile.getState())
                        .address(profile.getAddress())
                        .phoneNumber(profile.getPhoneNumber())
                        .occupation(profile.getOccupation())
                        .dateOfBirth(profile.getDateOfBirth() != null
                                ? profile.getDateOfBirth().toString()
                                : null)
                        .build())
                .build();
    }

    /**
     * Updates the profile of a user.
     *
     * @param id         ID of the user being updated.
     * @param email      Email of the currently authenticated user (to ensure
     *                   authorization).
     * @param requestDto Updated user profile details.
     * @return Updated user profile details as a DTO.
     */
    @Transactional
    public UserProfileResponse updateUserProfile(Long id, String email, UpdateUserRequest requestDto) {
        log.info("Updating user profile for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User with ID " + id + " not found",
                        HttpStatus.NOT_FOUND.value()));

        if (!user.getEmail().equals(email)) {
            log.warn("Unauthorized profile update attempt by email: {}", email);
            throw new CustomException("You are not authorized to edit this profile",
                    HttpStatus.FORBIDDEN.value());
        }

        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException("Profile for user ID " + id + " not found",
                        HttpStatus.NOT_FOUND.value()));

        updateProfileFields(profile, requestDto);

        log.info("Successfully updated profile for user ID: {}", id);

        return buildUserProfileResponse(user, profile);
    }

    private void updateProfileFields(Profile profile, UpdateUserRequest requestDto) {
        if (requestDto.getFirstName() != null)
            profile.setFirstName(requestDto.getFirstName());
        if (requestDto.getLastName() != null)
            profile.setLastName(requestDto.getLastName());
        if (requestDto.getPhoneNumber() != null)
            profile.setPhoneNumber(requestDto.getPhoneNumber());
        if (requestDto.getCountry() != null)
            profile.setCountry(requestDto.getCountry());
        if (requestDto.getOccupation() != null)
            profile.setOccupation(requestDto.getOccupation());
        if (requestDto.getAddress() != null)
            profile.setAddress(requestDto.getAddress());
        if (requestDto.getDateOfBirth() != null) {
            profile.setDateOfBirth(parseDate(requestDto.getDateOfBirth()));
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            log.error("Invalid date format: {}", date, e);
            throw new CustomException("Invalid date format: " + date, HttpStatus.BAD_REQUEST.value());
        }
    }

    private UserProfileResponse buildUserProfileResponse(User user, Profile profile) {
        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .accountType(user.getAccountType())
                .profile(UserProfileResponse.UserProfile.builder()
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .country(profile.getCountry())
                        .countryCode(profile.getCountryCode())
                        .state(profile.getState())
                        .address(profile.getAddress())
                        .phoneNumber(profile.getPhoneNumber())
                        .occupation(profile.getOccupation())
                        .dateOfBirth(profile.getDateOfBirth() != null
                                ? profile.getDateOfBirth().toString()
                                : null)
                        .build())
                .build();
    }
}
