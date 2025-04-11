package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.request.UpdateUserRequest;
import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.UserProfileResponse;
import com.mehedi.prismweather.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the current logged-in user's profile.
     *
     * @param principal The authenticated user's details (resolved automatically by Spring Security).
     * @return ResponseEntity containing the user profile data.
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(Principal principal) {
        String email = principal.getName();

        UserProfileResponse userProfile = userService.getCurrentUser(email);

        return ResponseEntity.ok(
            new ApiResponse<>(200, "Retrieved a current logged in profile successfully", userProfile)
        );
    }

    /**
     * Edit logged-in user's profile.
     *
     * @param id           The ID of the user to update.
     * @param requestDto   DTO containing updated user fields.
     * @return ResponseEntity with success or error message.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> editUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest requestDto,
            Principal principal) {

        // Ensure the user is authorized to update their own profile
        String currentUserEmail = principal.getName();
        UserProfileResponse updatedUser = userService.updateUserProfile(id, currentUserEmail, requestDto);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "User edited successfully", updatedUser)
        );
    }
}
