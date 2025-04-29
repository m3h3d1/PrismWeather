package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.alerts.WeatherAlertResponse;
import com.mehedi.prismweather.dto.ApiResponse;
import com.mehedi.prismweather.service.WeatherAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/locations")
public class WeatherAlertController {

    private final WeatherAlertService weatherAlertService;

    @Autowired
    public WeatherAlertController(WeatherAlertService weatherAlertService) {
        this.weatherAlertService = weatherAlertService;
    }

    @GetMapping("/{id}/alerts")
    public ResponseEntity<ApiResponse<WeatherAlertResponse>> getWeatherAlerts(
            @PathVariable Long id,
            Principal principal) {

        String userEmail = principal.getName();
        WeatherAlertResponse weatherAlertResponse = weatherAlertService.getWeatherAlerts(id, userEmail);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Retrieved weather alerts successfully", weatherAlertResponse)
        );
    }
}
