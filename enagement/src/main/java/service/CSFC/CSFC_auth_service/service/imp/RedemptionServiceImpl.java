package service.CSFC.CSFC_auth_service.service.imp;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.common.client.AuthServiceClient;
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
import java.util.stream.Collectors;

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
    private final AuthServiceClient authServiceClient;
    private final RedemptionMapper redemptionMapper;


    @Override
    @Transactional
    public RedemptionResponse confirmRedeem(Long rewardId, UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("CustomerIdInput are required");
        }

        //  Kiểm tra reward
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));

        if (!reward.getIsActive()) {
            throw new ResourceNotFoundException("Reward is inactive");
        }

        //  Kiểm tra điểm của user
        CustomerFranchise customerFranchise =
                customerFranchiseRepository.findByCustomerId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User loyalty not found"));

        if (customerFranchise.getCurrentPoints() < reward.getRequiredPoints()) {
            throw new InsufficientPointsException("Not enough points");
        }

        //  Kiểm tra rule cho redeem
        LoyaltyRule rule = loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION)
                .orElseThrow(() -> new RuntimeException("Redeem rule not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = rule.getExpiryDays() != null ? now.plusDays(rule.getExpiryDays()) : null;

        //  Trừ điểm của user
        customerFranchise.setCurrentPoints(
                customerFranchise.getCurrentPoints() - reward.getRequiredPoints()
        );
        customerFranchiseRepository.save(customerFranchise);

        //  Tạo redemption code
        String redemptionCode = "RDM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        //  Tạo PointTransaction liên kết CustomerFranchise
        PointTransaction pointTransaction = PointTransaction.builder()
                .customerFranchise(customerFranchise)
                .amount(-reward.getRequiredPoints())
                .actionType(ActionType.REDEEM)
                .build();

        pointTransactionRepository.save(pointTransaction);

        //  Tạo Redemption và gán PointTransaction
        Redemption redemption = Redemption.builder()
                .reward(reward)
                .pointsUsed(reward.getRequiredPoints())
                .status(PENDING)
                .redemptionCode(redemptionCode)
                .expiryDate(expiration)
                .pointTransaction(pointTransaction)
                .build();

        redemptionRepository.save(redemption);

        //  Cập nhật referenceId của PointTransaction (sử dụng redemption id)
        pointTransaction.setReferenceId("REDEEM_" + redemption.getId());
        pointTransactionRepository.saveAndFlush(pointTransaction);

        //  Generate QR code
        String qrBase64 = qrCodeService.generateQrBase64("REDEEM:" + redemptionCode);

        // Trả về response
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

        List<Redemption> redemptions = redemptionRepository.findAll();

        return redemptions.stream()
                .map(redemptionMapper::toResponse)
                .toList();
    }

    @Override
    public RedemptionResponse findById(Long id) {
        Redemption redemption = redemptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Redemption not found with id: " + id));

        return mapToResponse(redemption);
    }

    @Override
    public List<RedemptionResponse> getByUserId(UUID userId) {

        List<Redemption> redemptions =
                redemptionRepository.findByPointTransaction_CustomerFranchise_CustomerId(userId);

        return redemptions.stream()
                .map(redemptionMapper::toResponse)
                .toList();
    }

    private RedemptionResponse mapToResponse(Redemption r) {
        return RedemptionResponse.builder()
                .id(r.getId())
                .redemptionCode(r.getRedemptionCode())
                .userId(r.getPointTransaction().getCustomerFranchise().getCustomerId())
                .rewardId(r.getReward().getId())
                .promotionId(r.getPromotion() != null ? r.getPromotion().getId() : null)
                .status(r.getStatus())
                .pointsUsed(r.getPointsUsed())
                .expirationDate(r.getExpiryDate())
                .creationDate(r.getCreatedAt())
                .build();
    }
}
