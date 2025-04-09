package com.mehedi.prismweather.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetResponse {
    private String message;
    private UserData user;

    @Data
    @Builder
    public static class UserData {
        private String email;
        private String name;
    }
}
