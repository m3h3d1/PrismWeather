package com.mehedi.prismweather.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Latitude cannot be null")
    private Double lat;

    @NotNull(message = "Longitude cannot be null")
    private Double lon;

    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
