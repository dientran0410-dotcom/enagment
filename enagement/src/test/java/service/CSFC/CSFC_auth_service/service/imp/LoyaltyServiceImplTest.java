package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import service.CSFC.CSFC_auth_service.common.client.AuthServiceClient;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.CustomerStatus;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateLoyaltyTierRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.LoyaltyRuleRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyRule;
import service.CSFC.CSFC_auth_service.model.entity.PointTransaction;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    @Mock private LoyaltyTierRepository tierRepository;
    @Mock private CustomerFranchiseRepository customerFranchiseRepository;
    @Mock private PointTransactionRepository pointTransactionRepository;
    @Mock private LoyaltyRuleRepository ruleRepository;
    @Mock private RewardRepository rewardRepository;
    @Mock private AuthServiceClient authServiceClient;

    private UUID franchiseId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        franchiseId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    // ================= getCustomerEngagement =================

    @Test
    void getCustomerEngagement_success() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setFranchiseId(franchiseId);
        cf.setCurrentPoints(100);
        cf.setTotalEarnedPoints(200);
        cf.setStatus(CustomerStatus.ACTIVE);

        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(cf));

        CustomerEngagementResponse res =
                loyaltyService.getCustomerEngagement(customerId, franchiseId);

        assertEquals(100, res.getCurrentPoints());
    }

    // ================= registerCustomer =================

    @Test
    void registerCustomer_existingCustomer_returnExisting() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setFranchiseId(franchiseId);

        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(cf));

        CustomerEngagementResponse res =
                loyaltyService.registerCustomer(customerId, franchiseId, "jwt");

        assertEquals(customerId, res.getCustomerId());
        verify(customerFranchiseRepository, never()).save(any());
    }

    @Test
    void registerCustomer_newCustomer_createSuccess() {
        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        LoyaltyTier bronze = new LoyaltyTier();
        bronze.setName(TierName.BRONZE);

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.BRONZE))
                .thenReturn(Optional.of(bronze));

        when(authServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ApiResponse.success(new CustomerProfileResponse(), "ok"));

        when(customerFranchiseRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        CustomerEngagementResponse res =
                loyaltyService.registerCustomer(customerId, franchiseId, "jwt");

        assertEquals(0, res.getCurrentPoints());
    }

    // ================= createTier =================

    @Test
    void createTier_success() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(franchiseId);
        req.setName(TierName.GOLD);
        req.setBenefits("VIP");

        when(tierRepository.existsByFranchiseIdAndName(franchiseId, TierName.GOLD))
                .thenReturn(false);

        when(tierRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyTierResponse res = loyaltyService.createTier(req);

        assertEquals(TierName.GOLD, res.getName());
        assertEquals(1000, res.getMinPoint());
    }

    // ================= Rule =================

    @Test
    void createRule_fail_whenEventExists() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setEventType(EventType.ORDER);

        when(ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, EventType.ORDER, true))
                .thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void createRule_success() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Order Rule");
        req.setEventType(EventType.ORDER);
        req.setFixedPoints(10);

        when(ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, EventType.ORDER, true))
                .thenReturn(false);

        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyRuleResponse res =
                loyaltyService.createRule(franchiseId, req);

        assertEquals(EventType.ORDER, res.getEventType());
    }

    // ================= redeem =================

    @Test
    void redeem_success() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(500);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setRequiredPoints(200);
        reward.setIsActive(true);
        reward.setFranchiseId(franchiseId);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));

        when(rewardRepository.findById(10L))
                .thenReturn(Optional.of(reward));

        RedeemResponse res = loyaltyService.redeem(req, customerId);

        assertEquals(300, res.getCurrentPoints());
        verify(pointTransactionRepository).save(any());
    }

    @Test
    void redeem_fail_notEnoughPoints() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(50);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setRequiredPoints(200);
        reward.setIsActive(true);
        reward.setFranchiseId(franchiseId);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));

        when(rewardRepository.findById(10L))
                .thenReturn(Optional.of(reward));

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    // ================= ADDITIONAL EXCEPTION TESTS =================

    @Test
    void getCustomerEngagement_customerNotFound_throwException() {
        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> loyaltyService.getCustomerEngagement(customerId, franchiseId));
    }

    @Test
    void registerCustomer_nullCustomerId_throwException() {
        assertThrows(Exception.class,
                () -> loyaltyService.registerCustomer(null, franchiseId, "jwt"));
    }

    @Test
    void registerCustomer_nullFranchiseId_throwException() {
        assertThrows(Exception.class,
                () -> loyaltyService.registerCustomer(customerId, null, "jwt"));
    }


    @Test
    void createTier_alreadyExists_throwException() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(franchiseId);
        req.setName(TierName.GOLD);

        when(tierRepository.existsByFranchiseIdAndName(franchiseId, TierName.GOLD))
                .thenReturn(true);

        assertThrows(Exception.class, () -> loyaltyService.createTier(req));
    }

    @Test
    void createTier_nullFranchiseId_throwException() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(null);
        req.setName(TierName.GOLD);

        assertThrows(Exception.class, () -> loyaltyService.createTier(req));
    }

    @Test
    void createRule_nullFranchiseId_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setEventType(EventType.ORDER);

        assertThrows(Exception.class,
                () -> loyaltyService.createRule(null, req));
    }

    @Test
    void createRule_nullEventType_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setEventType(null);

        assertThrows(Exception.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void redeem_customerNotFound_throwException() {
        UUID cfId = UUID.randomUUID();

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.empty());

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        assertThrows(Exception.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    @Test
    void redeem_accessDenied_throwException() {
        UUID cfId = UUID.randomUUID();
        UUID otherCustomerId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(otherCustomerId);
        cf.setCurrentPoints(500);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));

        assertThrows(Exception.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    @Test
    void redeem_rewardNotFound_throwException() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(500);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(999L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));
        when(rewardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    @Test
    void redeem_rewardInactive_throwException() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(500);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setIsActive(false);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(reward));

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    @Test
    void redeem_exactPoints_success() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(200);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setRequiredPoints(200);
        reward.setIsActive(true);
        reward.setFranchiseId(franchiseId);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.save(any())).thenReturn(cf);
        when(pointTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RedeemResponse res = loyaltyService.redeem(req, customerId);

        assertEquals(0, cf.getCurrentPoints());
        assertEquals(200, res.getPointUsed());
        verify(pointTransactionRepository).save(any());
    }

    @Test
    void redeem_zeroPoints_shouldFail() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(0);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setRequiredPoints(100);
        reward.setIsActive(true);
        reward.setFranchiseId(franchiseId);

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(reward));

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    // ================= updateRule =================

    @Test
    void updateRule_success() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Updated Order Rule");
        req.setEventType(EventType.ORDER);
        req.setFixedPoints(15);
        req.setIsActive(true);

        LoyaltyRule existingRule = new LoyaltyRule();
        existingRule.setId(1L);
        existingRule.setFranchiseId(franchiseId);
        existingRule.setEventType(EventType.ORDER);
        existingRule.setName("Old Rule");
        existingRule.setFixedPoints(10);

        when(ruleRepository.findByFranchiseIdAndEventType(franchiseId, EventType.ORDER))
                .thenReturn(Optional.of(existingRule));
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyRuleResponse res = loyaltyService.updateRule(franchiseId, EventType.ORDER, req);

        assertEquals("Updated Order Rule", res.getName());
        assertEquals(15, res.getFixedPoints());
        verify(ruleRepository).save(any());
    }

    @Test
    void updateRule_notFound_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Rule");
        req.setEventType(EventType.ORDER);

        when(ruleRepository.findByFranchiseIdAndEventType(franchiseId, EventType.ORDER))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loyaltyService.updateRule(franchiseId, EventType.ORDER, req));
    }

    @Test
    void updateRule_invalidEventType_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setEventType(null);

        assertThrows(RuntimeException.class,
                () -> loyaltyService.updateRule(franchiseId, null, req));
    }

    @Test
    void updateRule_dateRequiredEventWithoutDates_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Birthday Rule");
        req.setEventType(EventType.BIRTHDAY);
        req.setStartDate(null);
        req.setEndDate(null);

        assertThrows(RuntimeException.class,
                () -> loyaltyService.updateRule(franchiseId, EventType.BIRTHDAY, req));
    }

    @Test
    void updateRule_startDateAfterEndDate_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Holiday Rule");
        req.setEventType(EventType.HOLIDAY);
        req.setStartDate(LocalDateTime.of(2026, 12, 31, 23, 59));
        req.setEndDate(LocalDateTime.of(2026, 1, 1, 0, 0));

        assertThrows(RuntimeException.class,
                () -> loyaltyService.updateRule(franchiseId, EventType.HOLIDAY, req));
    }

    // ================= updateTier =================

    @Test
    void updateTier_success() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(franchiseId);
        req.setName(TierName.SILVER);
        req.setBenefits("Updated Silver Benefits");

        LoyaltyTier existingTier = new LoyaltyTier();
        existingTier.setId(1L);
        existingTier.setFranchiseId(franchiseId);
        existingTier.setName(TierName.SILVER);
        existingTier.setMinPoint(500);
        existingTier.setBenefits("Old Benefits");

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.SILVER))
                .thenReturn(Optional.of(existingTier));
        when(tierRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyTierResponse res = loyaltyService.updateTier(franchiseId, TierName.SILVER, req);

        assertEquals(TierName.SILVER, res.getName());
        assertEquals("Updated Silver Benefits", res.getBenefits());
        verify(tierRepository).save(any());
    }

    @Test
    void updateTier_notFound_throwException() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(franchiseId);
        req.setName(TierName.GOLD);
        req.setBenefits("Benefits");

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.GOLD))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.updateTier(franchiseId, TierName.GOLD, req));
    }

    @Test
    void updateTier_nullBenefits_throwException() {
        CreateLoyaltyTierRequest req = new CreateLoyaltyTierRequest();
        req.setFranchiseId(franchiseId);
        req.setName(TierName.PLATINUM);
        req.setBenefits(null);

        LoyaltyTier existingTier = new LoyaltyTier();
        existingTier.setFranchiseId(franchiseId);
        existingTier.setName(TierName.PLATINUM);

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.PLATINUM))
                .thenReturn(Optional.of(existingTier));

        // Should allow null benefits update (business logic decision)
        when(tierRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyTierResponse res = loyaltyService.updateTier(franchiseId, TierName.PLATINUM, req);
        assertEquals(null, res.getBenefits());
    }

    // ================= deleteRule =================

    @Test
    void deleteRule_success() {
        LoyaltyRule rule = new LoyaltyRule();
        rule.setId(1L);
        rule.setFranchiseId(franchiseId);
        rule.setEventType(EventType.REVIEW);

        when(ruleRepository.findByFranchiseIdAndEventType(franchiseId, EventType.REVIEW))
                .thenReturn(Optional.of(rule));

        loyaltyService.deleteRule(franchiseId, EventType.REVIEW);

        verify(ruleRepository).delete(rule);
    }

    @Test
    void deleteRule_notFound_throwException() {
        when(ruleRepository.findByFranchiseIdAndEventType(franchiseId, EventType.REFERRAL))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loyaltyService.deleteRule(franchiseId, EventType.REFERRAL));
    }

    @Test
    void deleteRule_multipleTypes_deletedSuccessfully() {
        for (EventType type : new EventType[]{EventType.ORDER, EventType.REVIEW, EventType.REFERRAL}) {
            LoyaltyRule rule = new LoyaltyRule();
            rule.setEventType(type);
            rule.setFranchiseId(franchiseId);

            when(ruleRepository.findByFranchiseIdAndEventType(franchiseId, type))
                    .thenReturn(Optional.of(rule));

            loyaltyService.deleteRule(franchiseId, type);
            verify(ruleRepository).delete(rule);
        }
    }

    // ================= deleteTier =================

    @Test
    void deleteTier_success() {
        LoyaltyTier tier = new LoyaltyTier();
        tier.setId(1L);
        tier.setFranchiseId(franchiseId);
        tier.setName(TierName.SILVER);

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.SILVER))
                .thenReturn(Optional.of(tier));

        loyaltyService.deleteTier(franchiseId, TierName.SILVER);

        verify(tierRepository).delete(tier);
    }

    @Test
    void deleteTier_notFound_throwException() {
        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.GOLD))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loyaltyService.deleteTier(franchiseId, TierName.GOLD));
    }

    @Test
    void deleteTier_allTiers_deletedSuccessfully() {
        TierName[] tierNames = {TierName.BRONZE, TierName.SILVER, TierName.GOLD, TierName.PLATINUM};
        for (TierName tierName : tierNames) {
            LoyaltyTier tier = new LoyaltyTier();
            tier.setName(tierName);
            tier.setFranchiseId(franchiseId);

            when(tierRepository.findByFranchiseIdAndName(franchiseId, tierName))
                    .thenReturn(Optional.of(tier));

            loyaltyService.deleteTier(franchiseId, tierName);
            verify(tierRepository).delete(tier);
        }
    }

    // ================= getTransactionHistory =================

    @Test
    void getTransactionHistory_success() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setFranchiseId(franchiseId);

        PointTransaction pt1 = new PointTransaction();
        pt1.setId(1L);
        pt1.setAmount(100);
        pt1.setActionType(ActionType.EARN);

        PointTransaction pt2 = new PointTransaction();
        pt2.setId(2L);
        pt2.setAmount(-50);
        pt2.setActionType(ActionType.REDEEM);

        List<PointTransaction> transactions = new ArrayList<>();
        transactions.add(pt1);
        transactions.add(pt2);

        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(cf));
        when(pointTransactionRepository.findAllByCustomerFranchiseIdOrderByCreatedAtDesc(1L))
                .thenReturn(transactions);

        List<TransactionHistoryResponse> res =
                loyaltyService.getTransactionHistory(customerId, franchiseId);

        assertEquals(2, res.size());
        assertEquals(100, res.get(0).getAmount());
        assertEquals(-50, res.get(1).getAmount());
    }

    @Test
    void getTransactionHistory_empty() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setCustomerId(customerId);
        cf.setFranchiseId(franchiseId);

        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(cf));
        when(pointTransactionRepository.findAllByCustomerFranchiseIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        List<TransactionHistoryResponse> res =
                loyaltyService.getTransactionHistory(customerId, franchiseId);

        assertEquals(0, res.size());
    }

    @Test
    void getTransactionHistory_customerNotFound_throwException() {
        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.getTransactionHistory(customerId, franchiseId));
    }

    @Test
    void getTransactionHistory_invalidUUID_throwException() {
        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(null, franchiseId))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> loyaltyService.getTransactionHistory(null, franchiseId));
    }

    // ================= getAllCustomers =================

    @Test
    void getAllCustomers_success() {
        CustomerFranchise cf1 = new CustomerFranchise();
        cf1.setId(1L);
        cf1.setCustomerId(customerId);
        cf1.setFranchiseId(franchiseId);
        cf1.setCurrentPoints(100);
        cf1.setStatus(CustomerStatus.ACTIVE);

        CustomerFranchise cf2 = new CustomerFranchise();
        cf2.setId(2L);
        cf2.setCustomerId(UUID.randomUUID());
        cf2.setFranchiseId(franchiseId);
        cf2.setCurrentPoints(200);
        cf2.setStatus(CustomerStatus.ACTIVE);

        List<CustomerFranchise> customers = new ArrayList<>();
        customers.add(cf1);
        customers.add(cf2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerFranchise> page = new PageImpl<>(customers, pageable, 2);

        when(customerFranchiseRepository.findByFilters(franchiseId, null, pageable))
                .thenReturn(page);

        Page<CustomerEngagementResponse> res =
                loyaltyService.getAllCustomers(franchiseId, null, pageable);

        assertEquals(2, res.getContent().size());
        assertEquals(100, res.getContent().get(0).getCurrentPoints());
    }

    @Test
    void getAllCustomers_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerFranchise> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(customerFranchiseRepository.findByFilters(franchiseId, null, pageable))
                .thenReturn(emptyPage);

        Page<CustomerEngagementResponse> res =
                loyaltyService.getAllCustomers(franchiseId, null, pageable);

        assertEquals(0, res.getContent().size());
    }

    @Test
    void getAllCustomers_withTierId_success() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);
        cf.setFranchiseId(franchiseId);
        cf.setCurrentPoints(500);

        Pageable pageable = PageRequest.of(0, 10);
        Page<CustomerFranchise> page = new PageImpl<>(List.of(cf), pageable, 1);

        when(customerFranchiseRepository.findByFilters(franchiseId, 1L, pageable))
                .thenReturn(page);

        Page<CustomerEngagementResponse> res =
                loyaltyService.getAllCustomers(franchiseId, 1L, pageable);

        assertEquals(1, res.getContent().size());
    }

    @Test
    void getAllCustomers_pagination_secondPage() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(3L);
        cf.setFranchiseId(franchiseId);

        Pageable pageable = PageRequest.of(1, 2);
        Page<CustomerFranchise> page = new PageImpl<>(List.of(cf), pageable, 5);

        when(customerFranchiseRepository.findByFilters(franchiseId, null, pageable))
                .thenReturn(page);

        Page<CustomerEngagementResponse> res =
                loyaltyService.getAllCustomers(franchiseId, null, pageable);

        assertEquals(1, res.getContent().size());
        assertEquals(1, res.getNumber()); // page number
    }

    // ================= validateRule - Extended Branch Coverage =================

    @Test
    void validateRule_nullEventType_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setEventType(null);

        assertThrows(RuntimeException.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void validateRule_birthdayWithoutStartDate_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Birthday Rule");
        req.setEventType(EventType.BIRTHDAY);
        req.setStartDate(null);
        req.setEndDate(LocalDateTime.of(2026, 12, 31, 23, 59));

        assertThrows(RuntimeException.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void validateRule_holidayWithoutEndDate_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Holiday Rule");
        req.setEventType(EventType.HOLIDAY);
        req.setStartDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        req.setEndDate(null);

        assertThrows(RuntimeException.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void validateRule_specialEventDateMismatch_throwException() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Special Rule");
        req.setEventType(EventType.SPECIAL);
        req.setStartDate(LocalDateTime.of(2026, 6, 15, 12, 0));
        req.setEndDate(LocalDateTime.of(2026, 6, 10, 12, 0));

        assertThrows(RuntimeException.class,
                () -> loyaltyService.createRule(franchiseId, req));
    }

    @Test
    void validateRule_orderEventIgnoresDates() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Order Rule");
        req.setEventType(EventType.ORDER);
        req.setFixedPoints(10);
        req.setStartDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        req.setEndDate(LocalDateTime.of(2026, 12, 31, 23, 59));

        when(ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, EventType.ORDER, true))
                .thenReturn(false);
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyRuleResponse res = loyaltyService.createRule(franchiseId, req);

        assertEquals(EventType.ORDER, res.getEventType());
        assertEquals(null, res.getStartDate()); // dates should be cleared
        assertEquals(null, res.getEndDate());
    }

    @Test
    void validateRule_reviewEventIgnoresDates() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Review Rule");
        req.setEventType(EventType.REVIEW);
        req.setFixedPoints(5);
        req.setStartDate(LocalDateTime.now());
        req.setEndDate(LocalDateTime.now().plusDays(1));

        when(ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, EventType.REVIEW, true))
                .thenReturn(false);
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyRuleResponse res = loyaltyService.createRule(franchiseId, req);

        assertEquals(null, res.getStartDate());
        assertEquals(null, res.getEndDate());
    }

    @Test
    void validateRule_referralEventIgnoresDates() {
        LoyaltyRuleRequest req = new LoyaltyRuleRequest();
        req.setName("Referral Rule");
        req.setEventType(EventType.REFERRAL);
        req.setPointMultiplier(2.0);
        req.setStartDate(LocalDateTime.now());
        req.setEndDate(LocalDateTime.now().plusDays(30));

        when(ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, EventType.REFERRAL, true))
                .thenReturn(false);
        when(ruleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LoyaltyRuleResponse res = loyaltyService.createRule(franchiseId, req);

        assertEquals(null, res.getStartDate());
        assertEquals(null, res.getEndDate());
    }

    // ================= Additional Edge Cases =================

    @Test
    void redeem_rewardFranchiseIdNull_throwException() {
        UUID cfId = UUID.randomUUID();

        CustomerFranchise cf = new CustomerFranchise();
        cf.setCustomerId(customerId);
        cf.setCurrentPoints(500);

        Reward reward = new Reward();
        reward.setId(10L);
        reward.setIsActive(true);
        reward.setFranchiseId(null); // Null franchise

        RedeemRequest req = new RedeemRequest();
        req.setCustomerFranchiseId(cfId);
        req.setRewardId(10L);

        when(customerFranchiseRepository.findByCustomerIdForUpdate(cfId))
                .thenReturn(Optional.of(cf));
        when(rewardRepository.findById(10L)).thenReturn(Optional.of(reward));

        assertThrows(IllegalArgumentException.class,
                () -> loyaltyService.redeem(req, customerId));
    }

    @Test
    void registerCustomer_apiResponseNull_throwException() {
        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        when(authServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(null); // Null response

        assertThrows(IllegalArgumentException.class,
                () -> loyaltyService.registerCustomer(customerId, franchiseId, "jwt"));
    }

    @Test
    void registerCustomer_bronzeTierNotFound_throwException() {
        when(customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        when(authServiceClient.getCustomerProfile(any(), any()))
                .thenReturn(ApiResponse.success(new CustomerProfileResponse(), "ok"));

        when(tierRepository.findByFranchiseIdAndName(franchiseId, TierName.BRONZE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> loyaltyService.registerCustomer(customerId, franchiseId, "jwt"));
    }

    @Test
    void getTransactionHistory_multipleTransactions_orderedByCreatedAt() {
        CustomerFranchise cf = new CustomerFranchise();
        cf.setId(1L);

        PointTransaction pt1 = new PointTransaction();
        pt1.setId(1L);
        pt1.setAmount(100);

        PointTransaction pt2 = new PointTransaction();
        pt2.setId(2L);
        pt2.setAmount(50);

        PointTransaction pt3 = new PointTransaction();
        pt3.setId(3L);
        pt3.setAmount(-30);

        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(cf));
        when(pointTransactionRepository.findAllByCustomerFranchiseIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(pt3, pt2, pt1)); // Desc order

        List<TransactionHistoryResponse> res =
                loyaltyService.getTransactionHistory(customerId, franchiseId);

        assertEquals(3, res.size());
        assertEquals(-30, res.get(0).getAmount());
        assertEquals(50, res.get(1).getAmount());
        assertEquals(100, res.get(2).getAmount());
    }
}

