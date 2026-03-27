package hu.agoston.contractmanager.dto;

public record CompanyLookupResult(
        String name,
        String address,
        String taxNumber
) {
}
