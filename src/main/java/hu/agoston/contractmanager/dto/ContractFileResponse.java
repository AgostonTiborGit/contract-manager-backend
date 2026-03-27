package hu.agoston.contractmanager.dto;

import java.time.LocalDateTime;

public record ContractFileResponse(
        Long id,
        String displayName,
        String originalFileName,
        String contentType,
        long fileSize,
        LocalDateTime uploadedAt,
        boolean primaryFile
) {
}