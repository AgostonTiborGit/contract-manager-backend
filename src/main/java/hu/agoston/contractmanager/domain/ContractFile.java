package hu.agoston.contractmanager.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_files")
public class ContractFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "primary_file", nullable = false)
    private boolean primaryFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    protected ContractFile() {
        // JPA
    }

    public ContractFile(String displayName,
                        String originalFileName,
                        String storedFileName,
                        String storagePath,
                        String contentType,
                        long fileSize,
                        LocalDateTime uploadedAt,
                        boolean primaryFile,
                        Contract contract) {
        this.displayName = displayName;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.storagePath = storagePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.primaryFile = primaryFile;
        this.contract = contract;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public boolean isPrimaryFile() {
        return primaryFile;
    }

    public Contract getContract() {
        return contract;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPrimaryFile(boolean primaryFile) {
        this.primaryFile = primaryFile;
    }
}
