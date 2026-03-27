package hu.agoston.contractmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePartnerRequest(

        @NotBlank
        String name,

        @NotBlank
        @Pattern(
                regexp = "^[0-9\\-]+$",
                message = "Invalid tax number format"
        )
        String taxNumber,

        String address,
        String email,
        String phone
) {
}
