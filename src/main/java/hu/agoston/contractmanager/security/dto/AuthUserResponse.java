package hu.agoston.contractmanager.security.dto;

public record AuthUserResponse(
        String email,
        String role
) {
}
