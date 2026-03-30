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
import java.util.UUID;

public interface LoyaltyService {

    CustomerEngagementResponse getCustomerEngagement(UUID customerId, UUID franchiseId);

    List<TransactionHistoryResponse> getTransactionHistory(UUID customerId, UUID franchiseId);

    Page<CustomerEngagementResponse> getAllCustomers(
            UUID franchiseId,
            Long tierId,
            Pageable pageable
    );

    // ===== Customer Registration =====
    /**
     * Register a new customer to loyalty system
     * Called when a customer creates an account or first time joining a franchise
     */
    CustomerEngagementResponse registerCustomer(UUID customerId, UUID franchiseId, String jwtToken);

    // ===== Loyalty Tier =====
    LoyaltyTierResponse createTier(CreateLoyaltyTierRequest request);

    @Transactional(readOnly = true)
    List<LoyaltyTierResponse> getAllTiers();

    LoyaltyTierResponse updateTier(UUID franchiseId,
                                   TierName name,
                                   CreateLoyaltyTierRequest request);
    void deleteTier(UUID franchiseId, TierName tierName);
    //======  LoyaltyRule =======
    LoyaltyRuleResponse createRule(UUID franchiseId, LoyaltyRuleRequest request);
    List<LoyaltyRuleResponse> getAllRules();
    LoyaltyRuleResponse updateRule(UUID franchiseId, EventType eventType, LoyaltyRuleRequest request);
    void deleteRule(UUID franchiseId, EventType eventType);

    // ===== Redeem =====
    RedeemResponse redeem(RedeemRequest redeemRequest,UUID customerId);

    // ===== Earn Points =====
    void earnPoints(UUID customerId, UUID franchiseId, Integer points, String reason);
}