package com.finance.analytics.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {

    @NotBlank(message = "First name should not be empty")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Mobile number must be 10 digits and start with 6-9"
    )
    @Schema(description = "User's mobile number (10 digits starting with 6-9)", example = "9876543210")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|email\\.com|yahoo\\.com)$",
            message = "Email must end with @gmail.com, @email.com, or @yahoo.com"
    )
    @Schema(description = "User's email address", example = "john.doe@gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain uppercase, lowercase, number, and special character"
    )
    @Schema(description = "User's password (min 8 chars, must include upper, lower, digit, and special char)", example = "Password@123")
    private String password;

    @Schema(description = "List of role IDs to assign to the user")
    private List<UUID> roles;
}
