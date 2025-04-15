package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.LocationRequest;
import com.mehedi.prismweather.dto.response.GeocodingResponse;
import com.mehedi.prismweather.dto.response.LocationResponse;
import com.mehedi.prismweather.exception.CustomException;
import com.mehedi.prismweather.model.Location;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.LocationRepository;
import com.mehedi.prismweather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final GeocodingService geocodingService;
    private final UserRepository userRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository, GeocodingService geocodingService, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.geocodingService = geocodingService;
        this.userRepository = userRepository;
    }

    public LocationResponse addLocation(LocationRequest locationRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use the Geocoding service to resolve the latitude and longitude for the location name
        GeocodingResponse geocodingResponse = geocodingService.resolveCoordinates(locationRequest.getLocation());
        if (geocodingResponse == null || geocodingResponse.getLat() == null || geocodingResponse.getLon() == null) {
            throw new CustomException("Unable to resolve location coordinates", HttpStatus.BAD_REQUEST.value());
        }

        Location location = Location.builder()
                .location(locationRequest.getLocation())
                .title(locationRequest.getTitle())
                .lat(geocodingResponse.getLat())
                .lon(geocodingResponse.getLon())
                .createdAt(LocalDate.now())
                .user(user)
                .build();

        Location savedLocation = locationRepository.save(location);

        return LocationResponse.builder()
                .id(savedLocation.getId())
                .location(savedLocation.getLocation())
                .title(savedLocation.getTitle())
                .lat(savedLocation.getLat())
                .lon(savedLocation.getLon())
                .createdAt(savedLocation.getCreatedAt())
                .build();
    }

    public Page<LocationResponse> getAllLocationsByUser(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));

        Page<Location> locations = locationRepository.findByUser(user, pageable);

        return locations.map(location -> LocationResponse.builder()
                .id(location.getId())
                .location(location.getLocation())
                .title(location.getTitle())
                .lat(location.getLat())
                .lon(location.getLon())
                .createdAt(location.getCreatedAt())
                .build());
    }

    public void deleteLocationById(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));

        Location location = locationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException("Location not found or you don't have permission to delete it", HttpStatus.BAD_REQUEST.value()));

        locationRepository.delete(location);
    }
}
