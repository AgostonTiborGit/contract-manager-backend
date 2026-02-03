package hu.agoston.contractmanager.dto;

import java.time.LocalDate;

public record ContractResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        PartnerResponse partner
) {
}
