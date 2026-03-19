package service.CSFC.CSFC_auth_service.service.imp;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateLoyaltyTierRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.LoyaltyRuleRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;
import service.CSFC.CSFC_auth_service.model.entity.*;
import service.CSFC.CSFC_auth_service.repository.*;
import service.CSFC.CSFC_auth_service.service.LoyaltyService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static service.CSFC.CSFC_auth_service.model.constants.TierName.*;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyTierRepository tierRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final LoyaltyRuleRepository ruleRepository;

    private final RewardRepository rewardRepository;


    // ================= CUSTOMER =================

    @Override
    public CustomerEngagementResponse getCustomerEngagement(Long customerId, Long franchiseId) {
        CustomerFranchise cf = customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found in this franchise"));

        return CustomerEngagementResponse.builder()
                .id(cf.getId())
                .customerId(cf.getCustomerId())
                .franchiseId(cf.getFranchiseId())
                .currentPoints(cf.getCurrentPoints())
                .totalEarnedPoints(cf.getTotalEarnedPoints())
                .tierName(cf.getTier() != null ? cf.getTier().getName() : null)
                .status(cf.getStatus())
                .firstOrderAt(cf.getFirstOrderAt())
                .lastOrderAt(cf.getLastOrderAt())
                .createdAt(cf.getCreatedAt())
                .build();
    }

    @Override
    public List<TransactionHistoryResponse> getTransactionHistory(Long customerId, Long franchiseId) {
        CustomerFranchise cf = customerFranchiseRepository
                .findByCustomerIdAndFranchiseId(customerId, franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found in this franchise"));

        return pointTransactionRepository
                .findAllByCustomerFranchiseIdOrderByCreatedAtDesc(cf.getId())
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<@NonNull CustomerEngagementResponse> getAllCustomers(
            Long franchiseId,
            Long tierId,
            Pageable pageable) {

        return customerFranchiseRepository
                .findByFilters(franchiseId, tierId, pageable)
                .map(cf -> CustomerEngagementResponse.builder()
                        .id(cf.getId())
                        .customerId(cf.getCustomerId())
                        .franchiseId(cf.getFranchiseId())
                        .currentPoints(cf.getCurrentPoints())
                        .totalEarnedPoints(cf.getTotalEarnedPoints())
                        .tierName(cf.getTier() != null ? cf.getTier().getName() : null)
                        .status(cf.getStatus())
                        .firstOrderAt(cf.getFirstOrderAt())
                        .lastOrderAt(cf.getLastOrderAt())
                        .createdAt(cf.getCreatedAt())
                        .build());
    }

    private TransactionHistoryResponse mapToTransactionResponse(PointTransaction pt) {
        return TransactionHistoryResponse.builder()
                .id(pt.getId())
                .amount(pt.getAmount())
                .actionType(pt.getActionType())
                .referenceId(pt.getReferenceId())
                .createdAt(pt.getCreatedAt())
                .expiryDate(pt.getExpiryDate())
                .build();
    }

    // ================= TIER MANAGEMENT =================

    @Override
    @Transactional
    public LoyaltyTierResponse createTier(CreateLoyaltyTierRequest request) {

        if (tierRepository.existsByFranchiseIdAndName(
                request.getFranchiseId(), request.getName())) {
            throw new IllegalArgumentException("Tier name already exists in this franchise");
        }
        int minPoint = switch (request.getName()) {
            case BRONZE   -> 0;
            case SILVER   -> 500;
            case GOLD     -> 1000;
            case PLATINUM -> 2000;
        };

        LoyaltyTier tier = LoyaltyTier.builder()
                .franchiseId(request.getFranchiseId())
                .name(request.getName())
                .minPoint(minPoint)
                .benefits(request.getBenefits())
                .build();

        return mapToTierResponse(tierRepository.save(tier));
    }

    @Transactional(readOnly = true)
    @Override
    public List<LoyaltyTierResponse> getAllTiers() {
        return tierRepository.findAll()
                .stream()
//                .sorted(Comparator.comparing(LoyaltyTier::getFranchiseId)
//                        .thenComparing(LoyaltyTier::getTotalEarnedPoint))
                .map(this::mapToTierResponse)
                .toList();
    }

    @Override
    @Transactional
    public LoyaltyTierResponse updateTier(Long franchiseId,
                                          TierName name,
                                          CreateLoyaltyTierRequest request) {

        LoyaltyTier tier = tierRepository
                .findByFranchiseIdAndName(franchiseId, name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tier not found for this franchise"));

        // Chỉ update benefits
        tier.setBenefits(request.getBenefits());
        return mapToTierResponse(tierRepository.save(tier));
    }
    @Transactional
    @Override
    public void deleteTier(Long franchiseId, TierName tierName) {
        LoyaltyTier tier = tierRepository.findByFranchiseIdAndName(franchiseId, tierName)
                .orElseThrow(() -> new RuntimeException("Tier not found: " + tierName));
        tierRepository.delete(tier);
    }



    private LoyaltyTierResponse mapToTierResponse(LoyaltyTier tier) {
        return LoyaltyTierResponse.builder()
                .id(tier.getId())
                .franchiseId(tier.getFranchiseId())
                .name(tier.getName())
                .minPoint(tier.getMinPoint())
                .benefits(tier.getBenefits())
                .build();
    }
    //================= RuleService ===========
    @Override
    @Transactional
    public LoyaltyRuleResponse createRule(Long franchiseId, LoyaltyRuleRequest request) {
        validateRule(request);
        if (ruleRepository.existsByFranchiseIdAndEventTypeAndIsActive(
                franchiseId, request.getEventType(), true)) {
            throw new RuntimeException("EventType " + request.getEventType()
                    + " already exists and is active in this franchise");
        }
        LoyaltyRule rule = LoyaltyRule.builder()
                .franchiseId(franchiseId)
                .name(request.getName())
                .eventType(request.getEventType())
                .pointMultiplier(request.getPointMultiplier())
                .fixedPoints(request.getFixedPoints())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return mapToResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyRuleResponse> getAllRules() {
        return ruleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public LoyaltyRuleResponse updateRule(Long franchiseId, EventType eventType, LoyaltyRuleRequest request) {
        validateRule(request);
        LoyaltyRule rule = ruleRepository.findByFranchiseIdAndEventType(franchiseId, eventType)
                .orElseThrow(() -> new RuntimeException(
                        "Rule not found for franchiseId: " + franchiseId + ", eventType: " + eventType));

        rule.setName(request.getName());
        rule.setPointMultiplier(request.getPointMultiplier());
        rule.setFixedPoints(request.getFixedPoints());
        rule.setIsActive(request.getIsActive());
        rule.setStartDate(request.getStartDate());
        rule.setEndDate(request.getEndDate());

        return mapToResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional
    public void deleteRule(Long franchiseId, EventType eventType) {
        LoyaltyRule rule = ruleRepository.findByFranchiseIdAndEventType(franchiseId, eventType)
                .orElseThrow(() -> new RuntimeException(
                        "Rule not found for franchiseId: " + franchiseId + ", eventType: " + eventType));
        ruleRepository.delete(rule);
    }



    private static final List<EventType> DATE_REQUIRED_EVENTS =
            List.of(EventType.BIRTHDAY, EventType.HOLIDAY,EventType.SPECIAL);
    private void validateRule(LoyaltyRuleRequest request) {
        EventType eventType = request.getEventType(); // ← normalize
        request.setEventType(eventType);
        if (eventType == null) {
            throw new RuntimeException("Invalid eventType. Allowed values: " + Arrays.toString(EventType.values()));
        }

        if (DATE_REQUIRED_EVENTS.contains(eventType)) {
            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new RuntimeException("startDate and endDate are required for event: " + eventType);
            }
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new RuntimeException("startDate must be before endDate");
            }
        } else {
            // ORDER, REVIEW, REFERRAL → bỏ qua date dù có truyền lên
            request.setStartDate(null);
            request.setEndDate(null);
        }
    }


    private LoyaltyRuleResponse mapToResponse(LoyaltyRule rule) {
        return LoyaltyRuleResponse.builder()
                .id(rule.getId())
                .franchiseId(rule.getFranchiseId())
                .name(rule.getName())
                .eventType(rule.getEventType())
                .pointMultiplier(rule.getPointMultiplier())
                .fixedPoints(rule.getFixedPoints())
                .isActive(rule.getIsActive())
                .startDate(rule.getStartDate())
                .endDate(rule.getEndDate())
                .build();
    }


    // ================= REDEEM =================

    @Override
    @Transactional
    public RedeemResponse redeem(RedeemRequest request) {

        CustomerFranchise customerFranchise = customerFranchiseRepository
                .findByCustomerIdForUpdate(request.getCustomerFranchiseId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Reward reward = rewardRepository.findById(request.getRewardId())
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new ResourceNotFoundException("Reward is not active");
        }

        if (!reward.getFranchiseId()
                .equals(customerFranchise.getFranchiseId())) {
            throw new IllegalArgumentException("Reward does not belong to this franchise");
        }

        if (customerFranchise.getCurrentPoints() < reward.getRequiredPoints()) {
            throw new ResourceNotFoundException("Not enough loyalty points");
        }

        int remainingPoints = customerFranchise.getCurrentPoints() - reward.getRequiredPoints();
        customerFranchise.setCurrentPoints(remainingPoints);
        customerFranchiseRepository.save(customerFranchise);

        PointTransaction pointTransaction = PointTransaction.builder()
                .customerFranchise(customerFranchise)
                .amount(-reward.getRequiredPoints())
                .actionType(ActionType.REDEEM)
                .referenceId("REWARD" + reward.getId())
                .expiryDate(null)
                .build();

        pointTransactionRepository.save(pointTransaction);

        return RedeemResponse.builder()
                .redemptionCode("REWARD" + reward.getId())
                .pointUsed(reward.getRequiredPoints())
                .currentPoints(remainingPoints)
                .build();
    }
}