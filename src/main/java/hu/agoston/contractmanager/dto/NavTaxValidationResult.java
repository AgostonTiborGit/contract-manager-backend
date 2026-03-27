package hu.agoston.contractmanager.dto;

public record NavTaxValidationResult(
        boolean valid,
        String message
) {
}
