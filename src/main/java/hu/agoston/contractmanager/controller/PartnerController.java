package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.CreatePartnerRequest;
import hu.agoston.contractmanager.dto.PartnerResponse;
import hu.agoston.contractmanager.service.PartnerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    // USER + ADMIN
    @GetMapping
    public List<PartnerResponse> getAll() {
        return partnerService.getAll();
    }

    // USER + ADMIN
    @GetMapping("/{id}")
    public PartnerResponse getById(@PathVariable Long id) {
        return partnerService.getById(id);
    }

    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void create(@Valid @RequestBody CreatePartnerRequest request) {
        partnerService.create(request);
    }

    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        partnerService.delete(id);
    }
}
