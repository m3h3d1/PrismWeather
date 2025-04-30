package com.mehedi.prismweather.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyForecastEntry implements Serializable {
    private String date;
    private Double minTemperature;
    private Double maxTemperature;
    private Double averageHumidity;
    private String weatherDescription;
    private String weatherIcon;
}
