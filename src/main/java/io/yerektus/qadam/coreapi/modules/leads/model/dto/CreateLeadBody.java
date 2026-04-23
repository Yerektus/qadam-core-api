package io.yerektus.qadam.coreapi.modules.leads.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLeadBody(
    @JsonProperty("first_name") 
    @NotBlank
    @Size(min = 2, max = 50, message = "first_name must be between 2 and 50 characters ")
    String firstname,
    @JsonProperty("last_name")
    @NotBlank 
    @Size(min = 2, max = 50, message = "last_name must be between 2 and 50 characters")
    String lastname,
    @Email(message = "Invalid email address")
    @NotBlank
    String email,
    @NotBlank 
    @Size(min = 2, max = 100, message = "company must be between 2 and 100 characters")
    String company,
    @NotBlank 
    @Size(min = 2, max = 100, message = "city must be between 2 and 100 characters")
    String city,
    @JsonProperty("organization_type") 
    @NotBlank 
    @Size(min = 2, max = 100, message = "organization type must be between 2 and 100 characters")
    String organizationType,
    @NotBlank
    @Size(min = 2, max = 100, message = "source must be between 2 and 100 characters")
    String source
) {}
