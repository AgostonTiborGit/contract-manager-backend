package hu.agoston.contractmanager.service;

import hu.agoston.contractmanager.domain.Contract;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /* ---------- QUERY ---------- */

    @Transactional(readOnly = true)
    public List<ContractResponse> getAll() {
        return contractRepository.findAll()
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

    /* ---------- COMMAND ---------- */

    @Transactional
    public void create(CreateContractRequest request) {

        validateDates(request.startDate(), request.endDate());

        Partner partner = partnerRepository.findById(request.partnerId())
                .orElseThrow(() ->
                        new NotFoundException("Partner not found with id: " + request.partnerId())
                );

        Contract contract = new Contract(
                request.title(),
                request.startDate(),
                request.endDate(),
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

        boolean active = isActive(contract);

        // title mindig módosítható
        contract.setTitle(request.title());

        // aktív szerződésnél dátum TILOS
        if (active && (request.startDate() != null || request.endDate() != null)) {
            throw new BusinessException("Active contract dates cannot be modified");
        }

        // nem aktív → dátumok módosíthatók
        if (!active && request.startDate() != null && request.endDate() != null) {
            validateDates(request.startDate(), request.endDate());
            contract.setStartDate(request.startDate());
            contract.setEndDate(request.endDate());
        }
    }

    @Transactional
    public void delete(Long id) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Contract not found with id: " + id)
                );

        if (isActive(contract)) {
            throw new BusinessException("Active contract cannot be deleted");
        }

        contractRepository.delete(contract);
    }

    /* ---------- BUSINESS ---------- */

    private boolean isActive(Contract contract) {
        LocalDate today = LocalDate.now();
        return !today.isBefore(contract.getStartDate())
                && !today.isAfter(contract.getEndDate());
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (!startDate.isBefore(endDate)) {
            throw new BusinessException(
                    "Contract start date must be before end date"
            );
        }
    }

    /* ---------- MAPPERS ---------- */

    private ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getTitle(),
                contract.getStartDate(),
                contract.getEndDate(),
                toPartnerResponse(contract.getPartner())
        );
    }

    private ContractDetailsResponse toDetailsResponse(Contract contract) {
        return new ContractDetailsResponse(
                contract.getId(),
                contract.getTitle(),
                contract.getStartDate(),
                contract.getEndDate(),
                toPartnerResponse(contract.getPartner())
        );
    }

    private PartnerResponse toPartnerResponse(Partner partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getName(),
                partner.getEmail(),
                partner.getTaxNumber(),
                partner.getAddress()
        );
    }
}
