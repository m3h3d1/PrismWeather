package com.mehedi.prismweather.dto.alerts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAlertResponse {
    private double lat;
    private double lon;
    private String timezone;
    private List<AlertDetail> alerts; // List of active weather alerts
}
