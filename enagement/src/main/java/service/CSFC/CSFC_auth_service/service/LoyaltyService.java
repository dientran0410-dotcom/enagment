package service.CSFC.CSFC_auth_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateLoyaltyTierRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.LoyaltyRuleRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;

import java.util.List;

public interface LoyaltyService {

    CustomerEngagementResponse getCustomerEngagement(Long customerId, Long franchiseId);

    List<TransactionHistoryResponse> getTransactionHistory(Long customerId, Long franchiseId);

    Page<CustomerEngagementResponse> getAllCustomers(
            Long franchiseId,
            Long tierId,
            Pageable pageable
    );

    // ===== Loyalty Tier =====
    LoyaltyTierResponse createTier(CreateLoyaltyTierRequest request);

    @Transactional(readOnly = true)
    List<LoyaltyTierResponse> getAllTiers();

    LoyaltyTierResponse updateTier(Long franchiseId,
                                   TierName name,
                                   CreateLoyaltyTierRequest request);
    void deleteTier(Long franchiseId, TierName tierName);
    //======  LoyaltyRule =======
    LoyaltyRuleResponse createRule(Long franchiseId, LoyaltyRuleRequest request);
    List<LoyaltyRuleResponse> getAllRules();
    LoyaltyRuleResponse updateRule(Long franchiseId, EventType eventType, LoyaltyRuleRequest request);
    void deleteRule(Long franchiseId, EventType eventType);

    // ===== Redeem =====
    RedeemResponse redeem(RedeemRequest redeemRequest);
}