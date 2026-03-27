package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    /* ================= COUNT BY PARTNER ================= */

    // Megmondja, hogy egy partnerhez összesen hány szerződés tartozik.
    long countByPartnerId(Long partnerId);

    /* ================= ACTIVE CONTRACT COUNT ================= */

    // Aktívnak azt tekintjük, ahol a mai nap a kezdő és záró dátum közé esik.
    @Query("""
        select count(c)
        from Contract c
        where c.partner.id = :partnerId
          and :today between c.startDate and c.endDate
    """)
    long countActiveByPartnerId(
            @Param("partnerId") Long partnerId,
            @Param("today") LocalDate today
    );

    /* ================= EXPIRED CONTRACT COUNT ================= */

    // Lejárt szerződés: endDate kisebb, mint a mai nap.
    @Query("""
        select count(c)
        from Contract c
        where c.partner.id = :partnerId
          and c.endDate < :today
    """)
    long countExpiredByPartnerId(
            @Param("partnerId") Long partnerId,
            @Param("today") LocalDate today
    );

    /* ================= DELETE BY PARTNER ================= */

    // Partner törlésénél előbb az összes hozzá tartozó szerződést töröljük.
    @Modifying
    @Transactional
    @Query("""
        delete from Contract c
        where c.partner.id = :partnerId
    """)
    void deleteByPartnerId(@Param("partnerId") Long partnerId);

    /* ================= LIST BY PARTNER ================= */

    // Partnerhez tartozó szerződések lekérdezése, legfrissebb kezdési dátummal elöl.
    List<Contract> findByPartnerIdOrderByStartDateDesc(Long partnerId);
}