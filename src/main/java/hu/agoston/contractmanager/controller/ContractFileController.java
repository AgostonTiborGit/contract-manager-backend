package hu.agoston.contractmanager.controller;

import hu.agoston.contractmanager.dto.ContractFileResponse;
import hu.agoston.contractmanager.service.ContractFileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractFileController {

    private final ContractFileService contractFileService;

    public ContractFileController(ContractFileService contractFileService) {
        this.contractFileService = contractFileService;
    }

    /* ================= LIST FILES OF CONTRACT ================= */

    // Egy szerződéshez tartozó fájlok listázása.
    @GetMapping("/{contractId}/files")
    public List<ContractFileResponse> getFilesByContractId(@PathVariable Long contractId) {
        return contractFileService.getFilesByContractId(contractId);
    }

    /* ================= UPLOAD FILE ================= */

    // PDF feltöltés szerződéshez.
    @PostMapping(value = "/{contractId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ContractFileResponse upload(@PathVariable Long contractId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(name = "displayName", required = false) String displayName,
                                       @RequestParam(name = "primaryFile", required = false) Boolean primaryFile) {
        return contractFileService.upload(contractId, file, displayName, primaryFile);
    }

    /* ================= DOWNLOAD FILE ================= */

    // Fájl letöltése fileId alapján.
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        ContractFileResponse metadata = contractFileService.getFileMetadata(fileId);
        Resource resource = contractFileService.loadAsResource(fileId);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(metadata.originalFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(metadata.fileSize())
                .body(resource);
    }
}