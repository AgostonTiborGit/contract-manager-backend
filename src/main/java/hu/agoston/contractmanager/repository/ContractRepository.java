package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    // összes szerződés partnerhez
    long countByPartnerId(Long partnerId);

    // aktív szerződések száma
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

    // lejárt szerződések száma
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

    // partnerhez tartozó összes szerződés törlése
    @Modifying
    @Transactional
    @Query("""
        delete from Contract c
        where c.partner.id = :partnerId
    """)
    void deleteByPartnerId(@Param("partnerId") Long partnerId);
}
