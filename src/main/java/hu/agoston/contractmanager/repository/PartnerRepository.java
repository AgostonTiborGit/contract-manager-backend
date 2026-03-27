package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    boolean existsByTaxNumber(String taxNumber);

    boolean existsByTaxNumberAndIdNot(String taxNumber, Long id);

    Optional<Partner> findByTaxNumber(String taxNumber);
}