package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {

    // Tìm tier cao nhất mà customer đạt được dựa trên totalEarnedPoints
    @Query("SELECT t FROM LoyaltyTier t WHERE t.franchiseId = :franchiseId AND t.minPoint <= :points ORDER BY t.minPoint DESC LIMIT 1")
    Optional<LoyaltyTier> findHighestTierByPoints(Long franchiseId, Integer points);

    boolean existsByFranchiseIdAndName(Long franchiseId, TierName name);
    List<LoyaltyTier> findByFranchiseId(Long franchiseId);
    Optional<LoyaltyTier> findByFranchiseIdAndName(Long franchiseId, TierName name);
    void deleteById(Long id);
}