package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyRule;

import java.util.List;
import java.util.Optional;

public interface LoyaltyRuleRepository extends JpaRepository<LoyaltyRule, Long> {
    List<LoyaltyRule> findByFranchiseId(Long franchiseId);
    Optional<LoyaltyRule> findByFranchiseIdAndEventType(Long franchiseId, EventType eventType);
    boolean existsByFranchiseIdAndEventTypeAndIsActive(Long franchiseId, EventType eventType, Boolean isActive);
    Optional<LoyaltyRule> findByEventTypeAndIsActiveTrue(EventType eventType);
}