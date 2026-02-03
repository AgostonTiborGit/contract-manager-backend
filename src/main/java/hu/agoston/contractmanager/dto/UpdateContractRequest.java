package hu.agoston.contractmanager.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record UpdateContractRequest(
        @NotBlank String title,
        LocalDate startDate,
        LocalDate endDate
) {}
