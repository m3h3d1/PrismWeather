package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.WeatherResponse;
import com.mehedi.prismweather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/locations")
public class WeatherController {
    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<ApiResponse<WeatherResponse>> getCurrentWeather(
            @PathVariable Long id,
            Principal principal) {

        String userEmail = principal.getName();

        WeatherResponse weatherResponse = weatherService.getCurrentWeatherForLocation(id, userEmail);

        return ResponseEntity.ok(
                new ApiResponse<>(201, "Retrieved current weather by location successfully using OpenWeather API", weatherResponse)
        );
    }
}
