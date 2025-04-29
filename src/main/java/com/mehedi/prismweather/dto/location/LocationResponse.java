package com.mehedi.prismweather.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private Long id;
    private String location;
    private String title;
    private Double lat;
    private Double lon;
    private LocalDate createdAt;
}
