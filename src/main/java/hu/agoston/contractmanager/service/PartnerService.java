package hu.agoston.contractmanager.service;

import hu.agoston.contractmanager.domain.Partner;
import hu.agoston.contractmanager.dto.CreatePartnerRequest;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.exception.BusinessException;
import hu.agoston.contractmanager.exception.NotFoundException;
import hu.agoston.contractmanager.repository.ContractRepository;
import hu.agoston.contractmanager.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final ContractRepository contractRepository;

    public PartnerService(PartnerRepository partnerRepository,
                          ContractRepository contractRepository) {
        this.partnerRepository = partnerRepository;
        this.contractRepository = contractRepository;
    }

    /* ---------- QUERY ---------- */

    @Transactional(readOnly = true)
    public List<PartnerResponse> getAll() {
        return partnerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartnerResponse getById(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() ->
                        new NotFoundException("Partner not found with id: " + partnerId)
                );

        return toResponse(partner);
    }

    /* ---------- COMMAND ---------- */

    @Transactional
    public void create(CreatePartnerRequest request) {
        Partner partner = new Partner(
                request.name(),
                request.email(),
                request.taxNumber(),
                request.address()
        );
        partnerRepository.save(partner);
    }

    @Transactional
    public void delete(Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() ->
                        new NotFoundException("Partner not found with id: " + partnerId)
                );

        long contractCount = contractRepository.countByPartnerId(partnerId);

        if (contractCount > 0) {
            throw new BusinessException(
                    "Partner cannot be deleted because it has related contracts"
            );
        }

        partnerRepository.delete(partner);
    }

    /* ---------- MAPPER ---------- */

    private PartnerResponse toResponse(Partner partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getName(),
                partner.getEmail(),
                partner.getTaxNumber(),
                partner.getAddress()
        );
    }
}
