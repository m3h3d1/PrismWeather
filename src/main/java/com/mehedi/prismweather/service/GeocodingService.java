package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.response.GeocodingResponse;
import com.mehedi.prismweather.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final String openWeatherApiKey = "c26dca9c0e9b975e63a5077ae552e24b"; // Replace with your OpenWeather API key

    @Autowired
    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeocodingResponse resolveCoordinates(String locationName) {
        String url = String.format(
                "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                locationName, openWeatherApiKey
        );

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> results = response.getBody();
            if (results == null || results.isEmpty()) {
                throw new CustomException("Location not found", HttpStatus.BAD_REQUEST.value());
            }

            // Extract the first result (most relevant match)
            Map<String, Object> firstResult = results.get(0);
            Double lat = (Double) firstResult.get("lat");
            Double lon = (Double) firstResult.get("lon");

            return new GeocodingResponse(lat, lon);

        } catch (HttpClientErrorException ex) {
            throw new CustomException("Failed to resolve location: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}
