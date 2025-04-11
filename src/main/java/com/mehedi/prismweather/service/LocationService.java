package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.LocationRequest;
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
    private final UserRepository userRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    public LocationResponse addLocation(LocationRequest locationRequest, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location location = Location.builder()
                .location(locationRequest.getLocation())
                .title(locationRequest.getTitle())
                .createdAt(LocalDate.now())
                .user(user)
                .build();

        Location savedLocation = locationRepository.save(location);

        return LocationResponse.builder()
                .id(savedLocation.getId())
                .location(savedLocation.getLocation())
                .title(savedLocation.getTitle())
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
