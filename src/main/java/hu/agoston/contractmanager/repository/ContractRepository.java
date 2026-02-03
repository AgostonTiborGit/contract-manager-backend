package hu.agoston.contractmanager.repository;

import hu.agoston.contractmanager.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    long countByPartnerId(Long partnerId);
}
