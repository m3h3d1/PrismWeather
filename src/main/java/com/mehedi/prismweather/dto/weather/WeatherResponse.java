package com.mehedi.prismweather.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse implements Serializable {
    private Map<String, Object> coord;
    private List<Map<String, Object>> weather;
    private Map<String, Object> main;
    private Map<String, Object> wind;
    private Map<String, Object> clouds;
    private Map<String, Object> sys;
    private String name;
    private Integer timezone;
}
