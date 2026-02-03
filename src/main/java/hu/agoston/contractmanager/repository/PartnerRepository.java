package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
}
