package com.mehedi.prismweather.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    private String email;
}
