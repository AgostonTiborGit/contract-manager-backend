package hu.agoston.contractmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePartnerRequest(

        @NotBlank(message = "Partner name is required")
        @Size(max = 255, message = "Partner name must be at most 255 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Tax number is required")
        @Size(max = 50, message = "Tax number must be at most 50 characters")
        String taxNumber,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address
) {
}
