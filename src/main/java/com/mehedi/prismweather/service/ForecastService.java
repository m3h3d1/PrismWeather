package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.forecast.CityInfo;
import com.mehedi.prismweather.dto.forecast.DailyForecastEntry;
import com.mehedi.prismweather.dto.forecast.DailyForecastResponse;
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

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForecastService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final String openWeatherApiKey;

    @Autowired
    public ForecastService(LocationRepository locationRepository, UserRepository userRepository,
            RestTemplate restTemplate, @Value("${openweather.api.key}") String openWeatherApiKey) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.openWeatherApiKey = openWeatherApiKey;
    }

    public DailyForecastResponse getDailyForecast(Long locationId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));

        Location location = locationRepository.findByIdAndUser(locationId, user)
                .orElseThrow(
                        () -> new CustomException("Location not found or access denied", HttpStatus.FORBIDDEN.value()));

        String cityName = location.getLocation();

        String url = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&appid=%s",
                cityName, openWeatherApiKey);

        try {
            // Call the OpenWeather API to fetch 3-hourly forecast
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });

            Map<String, Object> forecastData = response.getBody();

            return parseDailyForecastData(forecastData);

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CustomException("City not found", HttpStatus.NOT_FOUND.value());
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new CustomException("Invalid API key for OpenWeather", HttpStatus.BAD_REQUEST.value());
            } else {
                throw new CustomException("Failed to fetch forecast data: " + ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value());
            }
        } catch (Exception ex) {
            throw new CustomException("An unexpected error occurred: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private DailyForecastResponse parseDailyForecastData(Map<String, Object> forecastData) {
        Map<String, Object> cityData = (Map<String, Object>) forecastData.get("city");
        Map<String, Object> coord = (Map<String, Object>) cityData.get("coord");

        CityInfo cityInfo = CityInfo.builder()
                .name((String) cityData.get("name"))
                .country((String) cityData.get("country"))
                .latitude(((Number) coord.get("lat")).doubleValue()) // Safely convert to Double
                .longitude(((Number) coord.get("lon")).doubleValue()) // Safely convert to Double
                .population((Integer) cityData.get("population"))
                .timezone((Integer) cityData.get("timezone"))
                .sunrise(Long.valueOf((Integer) cityData.get("sunrise")))
                .sunset(Long.valueOf((Integer) cityData.get("sunset")))
                .build();

        // Extract and group forecast entries by date
        List<Map<String, Object>> forecastList = (List<Map<String, Object>>) forecastData.get("list");

        Map<String, List<Map<String, Object>>> groupedByDate = forecastList.stream()
                .collect(Collectors.groupingBy(entry -> {
                    Long timestamp = Long.valueOf((Integer) entry.get("dt"));
                    return Instant.ofEpochSecond(timestamp)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString(); // Group by date (e.g., "2025-04-15")
                }));

        List<DailyForecastEntry> dailySummaries = groupedByDate.entrySet().stream()
                .map(entry -> summarizeDailyForecast(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailyForecastEntry::getDate)) // Sort by date
                .collect(Collectors.toList());

        return DailyForecastResponse.builder()
                .cityInfo(cityInfo)
                .forecast(dailySummaries)
                .build();
    }

    /**
     * Summarizes a single day's forecast data.
     *
     * @param date         The date for the forecast.
     * @param dailyEntries A list of 3-hourly forecast entries for the day.
     * @return The summarized daily forecast.
     */
    private DailyForecastEntry summarizeDailyForecast(String date, List<Map<String, Object>> dailyEntries) {
        DoubleSummaryStatistics tempStats = dailyEntries.stream()
                .map(entry -> (Map<String, Object>) entry.get("main"))
                .collect(Collectors.summarizingDouble(main -> ((Number) main.get("temp")).doubleValue())); // Safely
                                                                                                           // convert to
                                                                                                           // Double

        Double averageHumidity = dailyEntries.stream()
                .map(entry -> (Map<String, Object>) entry.get("main"))
                .mapToInt(main -> (Integer) main.get("humidity"))
                .average()
                .orElse(0);

        // Choose the first weather description/icon for simplicity
        Map<String, Object> representativeWeather = ((List<Map<String, Object>>) dailyEntries.get(0).get("weather"))
                .get(0);

        return DailyForecastEntry.builder()
                .date(date)
                .minTemperature(tempStats.getMin())
                .maxTemperature(tempStats.getMax())
                .averageHumidity(averageHumidity)
                .weatherDescription((String) representativeWeather.get("description"))
                .weatherIcon((String) representativeWeather.get("icon"))
                .build();
    }
}
