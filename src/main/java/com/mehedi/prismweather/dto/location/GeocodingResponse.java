package com.mehedi.prismweather.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponse implements Serializable {
    private Double lat;
    private Double lon;
}
