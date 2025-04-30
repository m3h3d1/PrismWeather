package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.location.GeocodingResponse;
import com.mehedi.prismweather.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
    private final String openWeatherApiKey;
    private final RateLimiterService rateLimiterService;

    @Autowired
    public GeocodingService(RestTemplate restTemplate, @Value("${openweather.api.key}") String openWeatherApiKey,
            RateLimiterService rateLimiterService) {
        this.restTemplate = restTemplate;
        this.openWeatherApiKey = openWeatherApiKey;
        this.rateLimiterService = rateLimiterService;
    }

    @Cacheable(value = "geocoding", key = "#locationName")
    public GeocodingResponse resolveCoordinates(String locationName) {
        String url = buildGeocodingUrl(locationName);

        rateLimiterService.checkRateLimit("geocoding_api");

        try {
            List<Map<String, Object>> results = fetchGeocodingResults(url);

            if (results == null || results.isEmpty()) {
                throw new CustomException("Location not found", HttpStatus.BAD_REQUEST.value());
            }

            return extractCoordinates(results.get(0));

        } catch (HttpClientErrorException ex) {
            throw new CustomException("Failed to resolve location: " + ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    private String buildGeocodingUrl(String locationName) {
        return String.format(
                "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                locationName, openWeatherApiKey);
    }

    private List<Map<String, Object>> fetchGeocodingResults(String url) {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    private GeocodingResponse extractCoordinates(Map<String, Object> result) {
        Double lat = (Double) result.get("lat");
        Double lon = (Double) result.get("lon");
        return new GeocodingResponse(lat, lon);
    }
}
