package hu.agoston.contractmanager.service.external;

import hu.agoston.contractmanager.dto.CompanyLookupResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DummyCompanyLookupService implements CompanyLookupService {

    @Override
    public Optional<CompanyLookupResult> findByTaxNumber(String taxNumber) {
        if ("12345678".equals(taxNumber)) {
            return Optional.of(
                    new CompanyLookupResult(
                            "Teszt Kft.",
                            "1111 Budapest, Teszt utca 1.",
                            taxNumber
                    )
            );
        }
        return Optional.empty();
    }
}
