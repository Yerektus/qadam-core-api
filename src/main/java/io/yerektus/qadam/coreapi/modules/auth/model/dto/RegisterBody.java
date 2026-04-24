package io.yerektus.qadam.coreapi.modules.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterBody(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message  = "Password must be between 8 and 72 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "Password must contain uppercase, lowercase, number and special character"
        )
        String password,
        @JsonProperty("first_name") 
        @NotBlank
        @Size(min = 2, max = 50, message = "first_name must be between 2 and 50 characters ")
        String firstname,
        @JsonProperty("last_name")
        @NotBlank 
        @Size(min = 2, max = 50, message = "last_name must be between 2 and 50 characters")
        String lastname,
        @JsonProperty("phone_number")
        @NotBlank
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format, for example +77071234567")
        String phoneNumber
) {}
