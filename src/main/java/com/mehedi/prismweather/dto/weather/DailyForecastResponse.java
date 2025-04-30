package com.mehedi.prismweather.dto.weather;

import com.mehedi.prismweather.dto.location.CityInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyForecastResponse implements Serializable {
    private CityInfo cityInfo;
    private List<DailyForecastEntry> forecast;
}
