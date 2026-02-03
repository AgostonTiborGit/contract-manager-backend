package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.ContractDetailsResponse;
import hu.agoston.contractmanager.dto.ContractResponse;
import hu.agoston.contractmanager.dto.CreateContractRequest;
import hu.agoston.contractmanager.dto.UpdateContractRequest;
import hu.agoston.contractmanager.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public List<ContractResponse> getAll() {
        return contractService.getAll();
    }

    @GetMapping("/{id}")
    public ContractDetailsResponse getById(@PathVariable Long id) {
        return contractService.getById(id);
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateContractRequest request) {
        contractService.create(request);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @Valid @RequestBody UpdateContractRequest request) {
        contractService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contractService.delete(id);
    }
}
