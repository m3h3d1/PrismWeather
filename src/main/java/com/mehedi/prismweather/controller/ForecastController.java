package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.forecast.DailyForecastResponse;
import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.ForecastResponse;
import com.mehedi.prismweather.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/locations")
public class ForecastController {
    private final ForecastService forecastService;

    @Autowired
    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping("/{id}/forecast")
    public ResponseEntity<ApiResponse<DailyForecastResponse>> getDailyForecast(
            @PathVariable Long id,
            Principal principal) {

        String userEmail = principal.getName();
        DailyForecastResponse forecastResponse = forecastService.getDailyForecast(id, userEmail);
        return ResponseEntity.ok(
                new ApiResponse<>(201, "Retrieved daily forecast by location successfully using OpenWeather API", forecastResponse)
        );
    }
}
