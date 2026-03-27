package hu.agoston.contractmanager.service;

import hu.agoston.contractmanager.domain.Partner;
import hu.agoston.contractmanager.dto.CompanyLookupResult;
import hu.agoston.contractmanager.dto.CreatePartnerRequest;
import hu.agoston.contractmanager.dto.NavTaxValidationResult;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.dto.PartnerWithStatsResponse;
import hu.agoston.contractmanager.exception.BusinessException;
import hu.agoston.contractmanager.exception.NotFoundException;
import hu.agoston.contractmanager.repository.ContractRepository;
import hu.agoston.contractmanager.repository.PartnerRepository;
import hu.agoston.contractmanager.service.external.CompanyLookupService;
import hu.agoston.contractmanager.service.external.NavTaxValidationService;
import hu.agoston.contractmanager.util.TaxNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final ContractRepository contractRepository;
    private final CompanyLookupService companyLookupService;
    private final NavTaxValidationService navTaxValidationService;

    /* ================= CREATE ================= */

    @Transactional
    public PartnerResponse create(CreatePartnerRequest request) {

        validateTaxNumberFormat(request.taxNumber());

        String normalizedTaxNumber =
                TaxNumberValidator.normalize(request.taxNumber());

        if (partnerRepository.existsByTaxNumber(normalizedTaxNumber)) {
            throw new BusinessException("Partner with this tax number already exists");
        }

        Partner partner = new Partner();
        partner.setName(request.name());
        partner.setTaxNumber(normalizedTaxNumber);
        partner.setAddress(request.address());
        partner.setEmail(request.email());
        partner.setPhone(request.phone());

        Partner saved = partnerRepository.save(partner);
        return toResponse(saved);
    }

    /* ================= UPDATE ================= */

    @Transactional
    public PartnerResponse update(Long id, CreatePartnerRequest request) {

        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Partner not found"));

        validateTaxNumberFormat(request.taxNumber());

        String normalizedTaxNumber =
                TaxNumberValidator.normalize(request.taxNumber());

        if (partnerRepository.existsByTaxNumberAndIdNot(normalizedTaxNumber, id)) {
            throw new BusinessException("Partner with this tax number already exists");
        }

        partner.setName(request.name());
        partner.setTaxNumber(normalizedTaxNumber);
        partner.setAddress(request.address());
        partner.setEmail(request.email());
        partner.setPhone(request.phone());

        Partner saved = partnerRepository.save(partner);
        return toResponse(saved);
    }

    /* ================= DELETE ================= */

    @Transactional
    public void delete(Long partnerId, String confirmation) {

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new NotFoundException("Partner not found"));

        long contractCount = contractRepository.countByPartnerId(partnerId);

        if (contractCount > 0 && !"IGEN".equalsIgnoreCase(confirmation)) {
            throw new BusinessException(
                    "Partner has contracts. Confirmation 'IGEN' is required to delete."
            );
        }

        contractRepository.deleteByPartnerId(partnerId);
        partnerRepository.delete(partner);
    }

    /* ================= LOOKUP (NAV CSAK ITT) ================= */

    @Transactional(readOnly = true)
    public CompanyLookupResult lookupByTaxNumber(String taxNumber) {

        validateTaxNumberFormat(taxNumber);

        String normalizedTaxNumber =
                TaxNumberValidator.normalize(taxNumber);

        if (partnerRepository.existsByTaxNumber(normalizedTaxNumber)) {
            throw new BusinessException("Partner with this tax number already exists");
        }

        validateTaxNumberWithNav(normalizedTaxNumber);

        return companyLookupService.findByTaxNumber(normalizedTaxNumber)
                .orElseThrow(() -> new NotFoundException("Company not found"));
    }

    /* ================= QUERIES ================= */

    @Transactional(readOnly = true)
    public List<PartnerResponse> findAll() {
        return partnerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartnerResponse findById(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Partner not found"));

        return toResponse(partner);
    }

    @Transactional(readOnly = true)
    public List<PartnerWithStatsResponse> findAllWithStats() {

        LocalDate today = LocalDate.now();

        return partnerRepository.findAll()
                .stream()
                .map(partner -> new PartnerWithStatsResponse(
                        partner.getId(),
                        partner.getName(),
                        partner.getTaxNumber(),
                        partner.getAddress(),
                        partner.getEmail(),
                        partner.getPhone(),
                        contractRepository.countActiveByPartnerId(partner.getId(), today),
                        contractRepository.countExpiredByPartnerId(partner.getId(), today)
                ))
                .toList();
    }

    /* ================= VALIDATION ================= */

    private void validateTaxNumberFormat(String taxNumber) {
        if (!TaxNumberValidator.isValid(taxNumber)) {
            throw new BusinessException("Invalid tax number format");
        }
    }

    private void validateTaxNumberWithNav(String normalizedTaxNumber) {
        NavTaxValidationResult navResult =
                navTaxValidationService.validate(normalizedTaxNumber);

        if (!navResult.valid()) {
            throw new BusinessException(
                    "NAV validation failed: " + navResult.message()
            );
        }
    }

    /* ================= MAPPER ================= */

    private PartnerResponse toResponse(Partner partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getName(),
                partner.getTaxNumber(),
                partner.getAddress(),
                partner.getEmail(),
                partner.getPhone()
        );
    }
}