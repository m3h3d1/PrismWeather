package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.response.WeatherResponse;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.Location;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.LocationRepository;
import com.mehedi.prismweather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final String openWeatherApiKey;

    @Autowired
    public WeatherService(LocationRepository locationRepository, UserRepository userRepository, RestTemplate restTemplate, @Value("${openweather.api.key}") String openWeatherApiKey) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.openWeatherApiKey = openWeatherApiKey;
    }

    public WeatherResponse getCurrentWeatherForLocation(Long locationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));

        Location location = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(() -> new CustomException("Location not found or access denied", HttpStatus.FORBIDDEN.value()));

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
                location.getLocation(),
                openWeatherApiKey
        );

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {});

            Map<String, Object> weatherData = response.getBody();
            return parseWeatherResponse(weatherData);
        } catch (HttpClientErrorException ex) {
            throw new CustomException("Failed to fetch weather information: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    private WeatherResponse parseWeatherResponse(Map<String, Object> weatherData) {
        return WeatherResponse.builder()
                .coord((Map<String, Object>) weatherData.get("coord"))
                .weather((List<Map<String, Object>>) weatherData.get("weather"))
                .main((Map<String, Object>) weatherData.get("main"))
                .wind((Map<String, Object>) weatherData.get("wind"))
                .clouds((Map<String, Object>) weatherData.get("clouds"))
                .sys((Map<String, Object>) weatherData.get("sys"))
                .name((String) weatherData.get("name"))
                .timezone((Integer) weatherData.get("timezone"))
                .build();
    }
}
