package hu.agoston.contractmanager.service.external;

import hu.agoston.contractmanager.dto.NavTaxValidationResult;
import org.springframework.stereotype.Service;

@Service
public class DummyNavTaxValidationService implements NavTaxValidationService {

    @Override
    public NavTaxValidationResult validate(String taxNumber) {
        if ("12345678".equals(taxNumber) || "87654321".equals(taxNumber)) {
            return new NavTaxValidationResult(true, "Valid tax number");
        }
        return new NavTaxValidationResult(false, "Tax number not found in NAV");
    }
}
