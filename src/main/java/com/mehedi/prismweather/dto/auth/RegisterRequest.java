package com.mehedi.prismweather.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Account type is required")
    private String accountType; // Freelancer or Company

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Country code is required")
    private String countryCode;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
