package com.mehedi.prismweather.controller;

import com.mehedi.prismweather.dto.auth.LocationRequest;
import com.mehedi.prismweather.dto.ApiResponse;
import com.mehedi.prismweather.dto.location.LocationResponse;
import com.mehedi.prismweather.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocations(
            Principal principal,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        String email = principal.getName();

        Page<LocationResponse> locations = locationService.getAllLocationsByUser(email, pageable);

        return ResponseEntity.ok(new ApiResponse<>(200, "Locations retrieved successfully", locations.getContent()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(
            @PathVariable Long id,
            Principal principal) {

        String userEmail = principal.getName();
        locationService.deleteLocationById(id, userEmail);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Location deleted successfully", null)
        );
    }
}
