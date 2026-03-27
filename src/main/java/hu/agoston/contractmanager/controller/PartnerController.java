package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.CompanyLookupResult;
import hu.agoston.contractmanager.dto.CreatePartnerRequest;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.dto.PartnerWithStatsResponse;
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

    @PostMapping
    public PartnerResponse create(@RequestBody @Valid CreatePartnerRequest request) {
        return partnerService.create(request);
    }

    @GetMapping
    public List<PartnerResponse> findAll() {
        return partnerService.findAll();
    }

    @GetMapping("/with-stats")
    public List<PartnerWithStatsResponse> findAllWithStats() {
        return partnerService.findAllWithStats();
    }

    @GetMapping("/lookup/{taxNumber}")
    public CompanyLookupResult lookup(@PathVariable String taxNumber) {
        return partnerService.lookupByTaxNumber(taxNumber);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam(name = "confirmation", required = false) String confirmation
    ) {
        partnerService.delete(id, confirmation);
    }

    @GetMapping("/{id}")
    public PartnerResponse findById(@PathVariable Long id) {
        return partnerService.findById(id);
    }
}
