package com.mehedi.prismweather.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {
    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Title is required")
    private String title;
}
