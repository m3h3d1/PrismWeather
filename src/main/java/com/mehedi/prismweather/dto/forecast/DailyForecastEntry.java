package com.mehedi.prismweather.dto.forecast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyForecastEntry {
    private String date;
    private Double minTemperature;
    private Double maxTemperature;
    private Double averageHumidity;
    private String weatherDescription;
    private String weatherIcon;
}
