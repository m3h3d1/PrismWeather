package com.mehedi.prismweather.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityInfo {
    private String name;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer population;
    private Integer timezone;
    private Long sunrise;
    private Long sunset;
}
