package hu.agoston.contractmanager.service.external;

import hu.agoston.contractmanager.dto.CompanyLookupResult;

import java.util.Optional;

public interface CompanyLookupService {

    Optional<CompanyLookupResult> findByTaxNumber(String taxNumber);
}
