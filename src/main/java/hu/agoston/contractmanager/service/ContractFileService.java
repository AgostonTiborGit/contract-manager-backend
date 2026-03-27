package hu.agoston.contractmanager.service;

import hu.agoston.contractmanager.domain.Contract;
import hu.agoston.contractmanager.domain.ContractFile;
import hu.agoston.contractmanager.dto.ContractFileResponse;
import hu.agoston.contractmanager.exception.BusinessException;
import hu.agoston.contractmanager.exception.NotFoundException;
import hu.agoston.contractmanager.repository.ContractFileRepository;
import hu.agoston.contractmanager.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContractFileService {

    private final ContractFileRepository contractFileRepository;
    private final ContractRepository contractRepository;
    private final Path storageRoot;

    public ContractFileService(ContractFileRepository contractFileRepository,
                               ContractRepository contractRepository,
                               @Value("${app.storage.contract-files-dir}") String storageDir) {
        this.contractFileRepository = contractFileRepository;
        this.contractRepository = contractRepository;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
    }

    /* ================= LIST FILES OF CONTRACT ================= */

    // Egy szerződéshez tartozó feltöltött fájlok listázása.
    @Transactional(readOnly = true)
    public List<ContractFileResponse> getFilesByContractId(Long contractId) {
        ensureContractExists(contractId);

        return contractFileRepository.findByContractIdOrderByUploadedAtDesc(contractId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* ================= UPLOAD FILE ================= */

    // Új PDF fájl feltöltése szerződéshez.
    @Transactional
    public ContractFileResponse upload(Long contractId,
                                       MultipartFile file,
                                       String displayName,
                                       Boolean primaryFile) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new NotFoundException("Contract not found with id: " + contractId)
                );

        validateFile(file);

        createStorageRootIfNeeded();

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + ".pdf";

        Path contractFolder = storageRoot.resolve(String.valueOf(contractId)).normalize();
        Path targetPath = contractFolder.resolve(storedFileName).normalize();

        try {
            Files.createDirectories(contractFolder);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException("Failed to store uploaded file");
        }

        boolean shouldBePrimary = resolvePrimaryFlag(contractId, primaryFile);

        if (shouldBePrimary) {
            contractFileRepository.clearPrimaryByContractId(contractId);
        }

        String normalizedDisplayName = normalize(displayName);

        ContractFile contractFile = new ContractFile(
                normalizedDisplayName,
                originalFileName,
                storedFileName,
                storageRoot.relativize(targetPath).toString(),
                resolveContentType(file),
                file.getSize(),
                LocalDateTime.now(),
                shouldBePrimary,
                contract
        );

        ContractFile saved = contractFileRepository.save(contractFile);
        return toResponse(saved);
    }

    /* ================= DOWNLOAD FILE ================= */

    // Fájl betöltése letöltéshez.
    @Transactional(readOnly = true)
    public Resource loadAsResource(Long fileId) {
        ContractFile contractFile = findFile(fileId);

        Path filePath = storageRoot.resolve(contractFile.getStoragePath()).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException("Stored file is missing or unreadable");
            }

            return resource;
        } catch (MalformedURLException ex) {
            throw new BusinessException("Stored file path is invalid");
        }
    }

    /* ================= FILE META ================= */

    // Letöltéshez vagy listához visszaadjuk a fájl metaadatait.
    @Transactional(readOnly = true)
    public ContractFileResponse getFileMetadata(Long fileId) {
        return toResponse(findFile(fileId));
    }

    /* ================= HELPERS ================= */

    private void ensureContractExists(Long contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new NotFoundException("Contract not found with id: " + contractId);
        }
    }

    private ContractFile findFile(Long fileId) {
        return contractFileRepository.findById(fileId)
                .orElseThrow(() ->
                        new NotFoundException("Contract file not found with id: " + fileId)
                );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File upload is required");
        }

        String originalFileName = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
            throw new BusinessException("Uploaded file name is invalid");
        }

        // Első körben csak PDF-et engedünk.
        if (!originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new BusinessException("Only PDF files are allowed");
        }
    }

    private void createStorageRootIfNeeded() {
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException ex) {
            throw new BusinessException("Failed to initialize file storage");
        }
    }

    private boolean resolvePrimaryFlag(Long contractId, Boolean requestedPrimaryFile) {
        if (requestedPrimaryFile != null) {
            return requestedPrimaryFile;
        }

        // Ha ez az első fájl, automatikusan legyen elsődleges.
        return contractFileRepository.countByContractId(contractId) == 0;
    }

    private String resolveContentType(MultipartFile file) {
        if (StringUtils.hasText(file.getContentType())) {
            return file.getContentType();
        }

        return "application/pdf";
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ContractFileResponse toResponse(ContractFile contractFile) {
        return new ContractFileResponse(
                contractFile.getId(),
                contractFile.getDisplayName(),
                contractFile.getOriginalFileName(),
                contractFile.getContentType(),
                contractFile.getFileSize(),
                contractFile.getUploadedAt(),
                contractFile.isPrimaryFile()
        );
    }
}