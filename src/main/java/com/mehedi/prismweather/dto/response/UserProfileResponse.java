package com.mehedi.prismweather.dto.response;

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
    private String name;
    private String email;
    private UserAddress address;

    @Data
    @Builder
    public static class UserAddress {
        private String country;
        private String state;
    }
}
