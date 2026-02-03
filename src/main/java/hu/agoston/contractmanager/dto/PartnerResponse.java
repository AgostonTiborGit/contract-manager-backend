package hu.agoston.contractmanager.dto;

public record PartnerResponse(
        Long id,
        String name,
        String email,
        String taxNumber,
        String address
) {
}
