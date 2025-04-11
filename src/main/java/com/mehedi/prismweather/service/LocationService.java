package com.mehedi.prismweather.service;

import com.mehedi.prismweather.dto.request.LocationRequest;
import com.mehedi.prismweather.dto.response.LocationResponse;
import com.mehedi.prismweather.model.Location;
import com.mehedi.prismweather.model.User;
import com.mehedi.prismweather.repository.LocationRepository;
import com.mehedi.prismweather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                .location(savedLocation.getLocation())
                .title(savedLocation.getTitle())
                .createdAt(savedLocation.getCreatedAt())
                .build();
    }
}
