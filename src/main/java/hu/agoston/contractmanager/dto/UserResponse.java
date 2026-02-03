package hu.agoston.contractmanager.dto;

import hu.agoston.contractmanager.domain.Role;

public record UserResponse(
        Long id,
        String email,
        Role role,
        boolean enabled
) {
}
