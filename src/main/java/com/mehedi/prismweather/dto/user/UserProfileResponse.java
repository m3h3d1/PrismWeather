package com.mehedi.prismweather.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String id;
    private String email;
    private String username;
    private String accountType;
    private UserProfile profile;

    @Data
    @Builder
    public static class UserProfile {
        private String firstName;
        private String lastName;
        private String country;
        private String countryCode;
        private String state;
        private String address;
        private String phoneNumber;
        private String occupation;
        private String dateOfBirth;
    }
}
