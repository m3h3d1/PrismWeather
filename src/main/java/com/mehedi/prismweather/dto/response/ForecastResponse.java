package com.mehedi.prismweather.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastResponse {
    private Double lat;
    private Double lon;
    private String timezone;
    private Integer timezoneOffset;
    private List<Map<String, Object>> daily;
}
