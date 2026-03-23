package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    List<LoyaltyRule> findByFranchiseId(UUID franchiseId);
    Optional<LoyaltyRule> findByFranchiseIdAndEventType(UUID franchiseId, EventType eventType);
    boolean existsByFranchiseIdAndEventTypeAndIsActive(UUID franchiseId, EventType eventType, Boolean isActive);
    Optional<LoyaltyRule> findByEventTypeAndIsActiveTrue(EventType eventType);
}