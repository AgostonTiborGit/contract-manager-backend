package hu.agoston.contractmanager.dto;

import hu.agoston.contractmanager.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role
) {
}
