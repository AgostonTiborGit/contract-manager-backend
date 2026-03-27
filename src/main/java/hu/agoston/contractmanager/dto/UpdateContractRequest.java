package hu.agoston.contractmanager.dto;

import hu.agoston.contractmanager.domain.ContractType;
import hu.agoston.contractmanager.domain.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateContractRequest(

        @NotBlank(message = "Title is required")
        String title,

        @Size(max = 255, message = "Reference number is too long")
        String referenceNumber,

        @NotNull(message = "Contract type is required")
        ContractType contractType,

        @NotNull(message = "Fixed term flag is required")
        Boolean fixedTerm,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        @PositiveOrZero(message = "Notice period days must be zero or greater")
        Integer noticePeriodDays,

        @Size(max = 2000, message = "Notes is too long")
        String notes,

        @PositiveOrZero(message = "Amount must be zero or greater")
        BigDecimal amount,

        Currency currency
) {
}