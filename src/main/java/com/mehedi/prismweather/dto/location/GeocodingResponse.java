package com.mehedi.prismweather.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponse {
    private Double lat;
    private Double lon;
}
