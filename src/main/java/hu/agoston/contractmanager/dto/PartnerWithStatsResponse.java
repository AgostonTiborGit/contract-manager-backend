package hu.agoston.contractmanager.dto;

public record PartnerWithStatsResponse(
        Long id,
        String name,
        String taxNumber,
        String address,
        String email,
        String phone,
        long activeContracts,
        long expiredContracts
) {}
