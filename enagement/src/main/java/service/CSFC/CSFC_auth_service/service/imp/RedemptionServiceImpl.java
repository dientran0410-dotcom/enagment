package service.CSFC.CSFC_auth_service.service.imp;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.exception.InsufficientPointsException;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.mapper.RedemptionMapper;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.*;
import service.CSFC.CSFC_auth_service.repository.*;
import service.CSFC.CSFC_auth_service.service.QrCodeService;
import service.CSFC.CSFC_auth_service.service.RedemptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static service.CSFC.CSFC_auth_service.model.constants.RedemptionStatus.PENDING;


@Service
@RequiredArgsConstructor
public class RedemptionServiceImpl implements RedemptionService {

    private final QrCodeService qrCodeService;
    private final RewardRepository rewardRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final RedemptionRepository redemptionRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final LoyaltyRuleRepository loyaltyRuleRepository;


    @Override
    @Transactional
    public RedemptionResponse confirmRedeem(Long rewardId) {
        Long userId = getCurrentUserId();

        //check reward
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new ResourceNotFoundException("Reward is inactive");
        }

        //check user point
        CustomerFranchise customerFranchise =
                customerFranchiseRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("User loyalty not found"));

        if (customerFranchise.getCurrentPoints() < reward.getRequiredPoints()) {
            throw new InsufficientPointsException("Not enough points");
        }

        //check redeem rule
        LoyaltyRule rule = loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION)
                .orElseThrow(() -> new RuntimeException("Redeem rule not found"));

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expiration = null;

        if(rule.getExpiryDays() != null) {
            expiration = now.plusDays(rule.getExpiryDays());
        }

        //deduct points
        customerFranchise.setCurrentPoints(
                customerFranchise.getCurrentPoints() - reward.getRequiredPoints()
        );

        customerFranchiseRepository.save(customerFranchise);

        //create redemption code
        String redemptionCode =
                "RDM-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        //save redemption
        Redemption redemption = Redemption.builder()
                .reward(reward)
                .pointsUsed(reward.getRequiredPoints())
                .status(PENDING)
                .redemptionCode(redemptionCode)
                .expiryDate(expiration)
                .build();

        redemptionRepository.save(redemption);

        //create point transaction
        PointTransaction pointTransaction = PointTransaction.builder()
                .customerFranchise(customerFranchise)
                .amount(-reward.getRequiredPoints())
                .actionType(ActionType.REDEEM)
                .referenceId("REDEEM_" + redemption.getId())
                .build();

        pointTransactionRepository.save(pointTransaction);

        redemption.setPointTransaction(pointTransaction);
        redemptionRepository.save(redemption);

        //generate QR code
        String qrContent =
                "REDEEM:" + redemptionCode;

        String qrBase64 =
                qrCodeService.generateQrBase64(qrContent);

        //return response
        return new RedemptionResponse(
                redemption.getId(),
                redemptionCode,
                userId,
                rewardId,
                null,
                reward.getRequiredPoints(),
                PENDING,
                expiration,
                now,
                qrBase64
        );
    }
    // giả lập lấy user login
    private Long getCurrentUserId() {
        return 1L;
    }

    @Override
    public Optional<Redemption> findByRedemptionCode(String code) {
        return redemptionRepository.findByRedemptionCode(code);
    }

    @Override
    public void save(Redemption redemption) {
        redemptionRepository.save(redemption);
    }

    @Override
    public List<RedemptionResponse> getAll() {
        List<Redemption> redemptionResponses = redemptionRepository.findAll();

        return redemptionResponses.stream()
                .map(RedemptionMapper::toResponse)
                .toList();
    }

    @Override
    public RedemptionResponse findById(Long id) {
        Redemption redemption = redemptionRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Redemption not found with id: " + id));

        return mapToResponse(redemption);

    }
    private RedemptionResponse mapToResponse(Redemption r) {
        return RedemptionResponse.builder()
                .id(r.getId())
                .redemptionCode(r.getRedemptionCode())
                .userId(r.getPointTransaction().getId())
                .rewardId(r.getReward().getId())
                .promotionId(r.getPromotion().getId())
                .status(r.getStatus())
                .pointsUsed(r.getPointsUsed())
                .expirationDate(r.getExpiryDate())
                .creationDate(r.getCreatedAt())
                .build();
    }
}
