package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    Optional<Redemption> findByRedemptionCode(String redemptionCode);
    List<Redemption> findByPointTransaction_CustomerFranchise_CustomerId(UUID customerId);
}
