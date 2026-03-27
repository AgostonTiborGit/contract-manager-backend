package hu.agoston.contractmanager.dto;

import hu.agoston.contractmanager.domain.ContractType;
import hu.agoston.contractmanager.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractDetailsResponse(
        Long id,
        String title,
        String referenceNumber,
        ContractType contractType,
        boolean fixedTerm,
        LocalDate startDate,
        LocalDate endDate,
        Integer noticePeriodDays,
        String notes,
        BigDecimal amount,
        Currency currency,
        PartnerResponse partner
) {
}