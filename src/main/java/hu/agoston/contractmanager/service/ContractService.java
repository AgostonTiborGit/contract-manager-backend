package hu.agoston.contractmanager.service;

import hu.agoston.contractmanager.domain.Contract;
import hu.agoston.contractmanager.domain.Currency;
import hu.agoston.contractmanager.domain.Partner;
import hu.agoston.contractmanager.dto.ContractDetailsResponse;
import hu.agoston.contractmanager.dto.ContractResponse;
import hu.agoston.contractmanager.dto.CreateContractRequest;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.dto.UpdateContractRequest;
import hu.agoston.contractmanager.exception.BusinessException;
import hu.agoston.contractmanager.exception.NotFoundException;
import hu.agoston.contractmanager.repository.ContractRepository;
import hu.agoston.contractmanager.repository.PartnerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final PartnerRepository partnerRepository;

    public ContractService(ContractRepository contractRepository,
                           PartnerRepository partnerRepository) {
        this.contractRepository = contractRepository;
        this.partnerRepository = partnerRepository;
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> getAll() {
        return contractRepository.findAll(Sort.by(Sort.Direction.DESC, "startDate"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContractDetailsResponse getById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Contract not found with id: " + id)
                );

        return toDetailsResponse(contract);
    }

    @Transactional
    public void create(CreateContractRequest request) {
        validateBusinessRules(
                request.fixedTerm(),
                request.startDate(),
                request.endDate(),
                request.noticePeriodDays(),
                request.amount(),
                request.currency()
        );

        Partner partner = partnerRepository.findById(request.partnerId())
                .orElseThrow(() ->
                        new NotFoundException("Partner not found with id: " + request.partnerId())
                );

        Contract contract = new Contract(
                request.title().trim(),
                normalize(request.referenceNumber()),
                request.contractType(),
                request.fixedTerm(),
                request.startDate(),
                request.endDate(),
                request.noticePeriodDays(),
                normalize(request.notes()),
                request.amount(),
                request.currency(),
                partner
        );

        contractRepository.save(contract);
    }

    @Transactional
    public void update(Long id, UpdateContractRequest request) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Contract not found with id: " + id)
                );

        validateBusinessRules(
                request.fixedTerm(),
                request.startDate(),
                request.endDate(),
                request.noticePeriodDays(),
                request.amount(),
                request.currency()
        );

        contract.setTitle(request.title().trim());
        contract.setReferenceNumber(normalize(request.referenceNumber()));
        contract.setContractType(request.contractType());
        contract.setFixedTerm(request.fixedTerm());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setNoticePeriodDays(request.noticePeriodDays());
        contract.setNotes(normalize(request.notes()));
        contract.setAmount(request.amount());
        contract.setCurrency(request.currency());
    }

    @Transactional
    public void delete(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Contract not found with id: " + id)
                );

        contractRepository.delete(contract);
    }

    private void validateBusinessRules(Boolean fixedTerm,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       Integer noticePeriodDays,
                                       BigDecimal amount,
                                       Currency currency) {

        if (fixedTerm == null) {
            throw new BusinessException("Fixed term flag is required");
        }

        if (startDate == null) {
            throw new BusinessException("Start date is required");
        }

        if (Boolean.TRUE.equals(fixedTerm)) {
            if (endDate == null) {
                throw new BusinessException("End date is required for fixed-term contracts");
            }

            if (!startDate.isBefore(endDate)) {
                throw new BusinessException("Contract start date must be before end date");
            }
        } else {
            if (endDate != null) {
                throw new BusinessException("End date must be empty for indefinite contracts");
            }
        }

        if (noticePeriodDays != null && noticePeriodDays < 0) {
            throw new BusinessException("Notice period days must be zero or greater");
        }

        boolean amountProvided = amount != null;
        boolean currencyProvided = currency != null;

        if (amountProvided != currencyProvided) {
            throw new BusinessException("Amount and currency must be filled together");
        }

        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Amount must be zero or greater");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getTitle(),
                contract.getReferenceNumber(),
                contract.getContractType(),
                contract.isFixedTerm(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getNoticePeriodDays(),
                contract.getNotes(),
                contract.getAmount(),
                contract.getCurrency(),
                toPartnerResponse(contract.getPartner())
        );
    }

    private ContractDetailsResponse toDetailsResponse(Contract contract) {
        return new ContractDetailsResponse(
                contract.getId(),
                contract.getTitle(),
                contract.getReferenceNumber(),
                contract.getContractType(),
                contract.isFixedTerm(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getNoticePeriodDays(),
                contract.getNotes(),
                contract.getAmount(),
                contract.getCurrency(),
                toPartnerResponse(contract.getPartner())
        );
    }

    private PartnerResponse toPartnerResponse(Partner partner) {
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