package com.mehedi.prismweather.dto.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityInfo implements Serializable {
    private String name;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer population;
    private Integer timezone;
    private Long sunrise;
    private Long sunset;
}
