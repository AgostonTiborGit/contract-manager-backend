package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.CompanyLookupResult;
import hu.agoston.contractmanager.dto.ContractResponse;
import hu.agoston.contractmanager.dto.CreatePartnerRequest;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.dto.PartnerWithStatsResponse;
import hu.agoston.contractmanager.service.ContractService;
import hu.agoston.contractmanager.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;
    private final ContractService contractService;

    /* ================= CREATE ================= */

    @PostMapping
    public PartnerResponse create(@RequestBody @Valid CreatePartnerRequest request) {
        return partnerService.create(request);
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public PartnerResponse update(@PathVariable Long id,
                                  @RequestBody @Valid CreatePartnerRequest request) {
        return partnerService.update(id, request);
    }

    /* ================= LIST ================= */

    @GetMapping
    public List<PartnerResponse> findAll() {
        return partnerService.findAll();
    }

    /* ================= LIST WITH STATS ================= */

    @GetMapping("/with-stats")
    public List<PartnerWithStatsResponse> findAllWithStats() {
        return partnerService.findAllWithStats();
    }

    /* ================= COMPANY LOOKUP ================= */

    @GetMapping("/lookup/{taxNumber}")
    public CompanyLookupResult lookup(@PathVariable String taxNumber) {
        return partnerService.lookupByTaxNumber(taxNumber);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam(name = "confirmation", required = false) String confirmation
    ) {
        partnerService.delete(id, confirmation);
    }

    /* ================= SINGLE PARTNER ================= */

    @GetMapping("/{id}")
    public PartnerResponse findById(@PathVariable Long id) {
        return partnerService.findById(id);
    }

    /* ================= PARTNER CONTRACTS ================= */

    // Ez lesz a partnerhez kötött szerződéslista alap endpointja.
    @GetMapping("/{id}/contracts")
    public List<ContractResponse> getContractsByPartner(@PathVariable Long id) {
        return contractService.getByPartnerId(id);
    }
}