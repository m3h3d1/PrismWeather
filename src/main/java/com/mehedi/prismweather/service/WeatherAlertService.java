package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.alerts.AlertDetail;
import com.mehedi.prismweather.dto.alerts.WeatherAlertResponse;
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

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WeatherAlertService {

    private final RestTemplate restTemplate;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final String weatherApiKey;
    private final RateLimiterService rateLimiterService;

    @Autowired
    public WeatherAlertService(RestTemplate restTemplate, LocationRepository locationRepository,
            UserRepository userRepository, @Value("${weatherapi.key}") String weatherApiKey,
            RateLimiterService rateLimiterService) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.weatherApiKey = weatherApiKey;
        this.rateLimiterService = rateLimiterService;
    }

    public WeatherAlertResponse getWeatherAlerts(Long locationId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Location location = getLocationByIdAndUser(locationId, user);

        String apiUrl = buildWeatherApiUrl(location);

        rateLimiterService.checkRateLimit("weather_alert_api:" + userEmail);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            return parseWeatherAlertResponse(response.getBody());
        } catch (HttpClientErrorException ex) {
            throw new CustomException("Failed to fetch weather alerts: " + ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value());
        }
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
    }

    private Location getLocationByIdAndUser(Long locationId, User user) {
        return locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(
                        () -> new CustomException("Location not found or access denied", HttpStatus.FORBIDDEN.value()));
    }

    private String buildWeatherApiUrl(Location location) {
        return String.format(
                "http://api.weatherapi.com/v1/forecast.json?key=%s&q=%s,%s&alerts=yes",
                weatherApiKey, location.getLat(), location.getLon());
    }

    private WeatherAlertResponse parseWeatherAlertResponse(Map<String, Object> responseData) {
        Map<String, Object> locationData = (Map<String, Object>) responseData.get("location");
        Double lat = (Double) locationData.get("lat");
        Double lon = (Double) locationData.get("lon");
        String timezone = (String) locationData.get("tz_id");

        List<AlertDetail> alerts = parseAlerts((Map<String, Object>) responseData.get("alerts"));

        return WeatherAlertResponse.builder()
                .lat(lat)
                .lon(lon)
                .timezone(timezone)
                .alerts(alerts)
                .build();
    }

    private List<AlertDetail> parseAlerts(Map<String, Object> alertsData) {
        List<Map<String, Object>> alertsRaw = (List<Map<String, Object>>) alertsData.get("alert");
        List<AlertDetail> alerts = new ArrayList<>();

        if (alertsRaw != null) {
            for (Map<String, Object> alert : alertsRaw) {
                alerts.add(parseAlertDetail(alert));
            }
        }

        return alerts;
    }

    private AlertDetail parseAlertDetail(Map<String, Object> alert) {
        String effectiveStr = (String) alert.get("effective");
        String expiresStr = (String) alert.get("expires");

        return AlertDetail.builder()
                .senderName((String) alert.get("headline"))
                .event((String) alert.get("event"))
                .start(parseIsoDateToEpoch(effectiveStr))
                .end(parseIsoDateToEpoch(expiresStr))
                .description((String) alert.get("desc"))
                .build();
    }

    private Long parseIsoDateToEpoch(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(isoDate).toEpochSecond();
        } catch (DateTimeParseException e) {
            throw new CustomException("Failed to parse date: " + isoDate, HttpStatus.BAD_REQUEST.value());
        }
    }
}
