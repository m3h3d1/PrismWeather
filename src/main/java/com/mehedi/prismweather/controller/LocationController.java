package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.request.LocationRequest;
import com.mehedi.prismweather.dto.response.ApiResponse;
import com.mehedi.prismweather.dto.response.LocationResponse;
import com.mehedi.prismweather.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> addLocation(@Valid @RequestBody LocationRequest locationRequest, Principal principal) {
        String email = principal.getName();
        LocationResponse response = locationService.addLocation(locationRequest, email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Location added successfully", response));
    }
}
