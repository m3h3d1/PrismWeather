package com.mehedi.prismweather.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String token;
    private UserData user;

    @Data
    @Builder
    public static class UserData {
        private String email;
        private String name;
        private String accountType;
        private String country;
        private String countryCode;
        private String state;
        private String address;
        private String phoneNumber;
    }
}
