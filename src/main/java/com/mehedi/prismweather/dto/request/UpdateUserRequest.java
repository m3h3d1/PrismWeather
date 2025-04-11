package com.mehedi.prismweather.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number")
    private String phoneNumber;

    private String country;

    private String occupation;

    private String address;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date of birth must follow the format yyyy-MM-dd")
    private String dateOfBirth;
}
