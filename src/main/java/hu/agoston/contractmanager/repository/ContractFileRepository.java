package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.ContractFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractFileRepository extends JpaRepository<ContractFile, Long> {

    /* ================= LIST BY CONTRACT ================= */

    // Egy szerződéshez tartozó fájlok lekérdezése.
    List<ContractFile> findByContractIdOrderByUploadedAtDesc(Long contractId);

    /* ================= COUNT BY CONTRACT ================= */

    // Megmondja, hogy egy szerződéshez hány fájl tartozik.
    long countByContractId(Long contractId);

    /* ================= CLEAR PRIMARY FLAG ================= */

    // Ha új elsődleges fájlt állítunk be, a többi fájlról levesszük a primary jelölést.
    @Modifying
    @Query("""
        update ContractFile cf
        set cf.primaryFile = false
        where cf.contract.id = :contractId
    """)
    void clearPrimaryByContractId(@Param("contractId") Long contractId);
}