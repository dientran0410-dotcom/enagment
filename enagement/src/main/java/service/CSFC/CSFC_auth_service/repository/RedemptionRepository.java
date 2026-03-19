package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.Optional;


@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    Optional<Redemption> findByRedemptionCode(String redemptionCode);
}
