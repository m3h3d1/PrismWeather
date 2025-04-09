package com.mehedi.prismweather.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "account_type", nullable = false)
    private String accountType; // Freelancer or Company

    @Column(nullable = false, length = 50)
    private String country;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.USER; // Default role is USER.

    public enum Role {
        USER,
        ADMIN
    }
}
