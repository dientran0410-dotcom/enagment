package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import service.CSFC.CSFC_auth_service.model.entity.Reward;

import java.util.List;
import java.util.UUID;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByIsActiveTrue();

    List<Reward> findByFranchiseIdAndIsActiveTrue(UUID franchiseId);
}
