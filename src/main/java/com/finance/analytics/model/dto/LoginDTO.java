package com.finance.analytics.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class LoginDTO {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "admin@finance.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "Admin@123")
    private String password;
}
