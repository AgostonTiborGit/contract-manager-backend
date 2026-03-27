package hu.agoston.contractmanager.service.external;

import hu.agoston.contractmanager.dto.NavTaxValidationResult;

public interface NavTaxValidationService {

    NavTaxValidationResult validate(String taxNumber);
}
