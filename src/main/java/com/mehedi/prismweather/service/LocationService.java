package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.auth.LocationRequest;
import com.mehedi.prismweather.dto.location.GeocodingResponse;
import com.mehedi.prismweather.dto.location.LocationResponse;
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
    public LocationService(LocationRepository locationRepository, GeocodingService geocodingService,
            UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.geocodingService = geocodingService;
        this.userRepository = userRepository;
    }

    public LocationResponse addLocation(LocationRequest locationRequest, String email) {
        User user = getUserByEmail(email);

        GeocodingResponse geocodingResponse = geocodingService.resolveCoordinates(locationRequest.getLocation());
        validateGeocodingResponse(geocodingResponse);

        Location location = buildLocation(locationRequest, geocodingResponse, user);
        Location savedLocation = locationRepository.save(location);

        return buildLocationResponse(savedLocation);
    }

    public Page<LocationResponse> getAllLocationsByUser(String userEmail, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        Page<Location> locations = locationRepository.findByUser(user, pageable);

        return locations.map(this::buildLocationResponse);
    }

    public void deleteLocationById(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);
        Location location = locationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CustomException("Location not found or you don't have permission to delete it",
                        HttpStatus.BAD_REQUEST.value()));

        locationRepository.delete(location);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
    }

    private void validateGeocodingResponse(GeocodingResponse geocodingResponse) {
        if (geocodingResponse == null || geocodingResponse.getLat() == null || geocodingResponse.getLon() == null) {
            throw new CustomException("Unable to resolve location coordinates", HttpStatus.BAD_REQUEST.value());
        }
    }

    private Location buildLocation(LocationRequest locationRequest, GeocodingResponse geocodingResponse, User user) {
        return Location.builder()
                .location(locationRequest.getLocation())
                .title(locationRequest.getTitle())
                .lat(geocodingResponse.getLat())
                .lon(geocodingResponse.getLon())
                .createdAt(LocalDate.now())
                .user(user)
                .build();
    }

    private LocationResponse buildLocationResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .location(location.getLocation())
                .title(location.getTitle())
                .lat(location.getLat())
                .lon(location.getLon())
                .createdAt(location.getCreatedAt())
                .build();
    }
}
