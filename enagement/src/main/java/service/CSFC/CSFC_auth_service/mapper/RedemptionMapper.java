package service.CSFC.CSFC_auth_service.mapper;

import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

public class RedemptionMapper {
    public static RedemptionResponse toResponse(Redemption redemption) {

        Long rewardId = redemption.getReward() != null
                ? redemption.getReward().getId()
                : null;

        Long promotionId = redemption.getPromotion() != null
                ? redemption.getPromotion().getId()
                : null;

        Long userId = redemption.getPointTransaction() != null
                ? redemption.getPointTransaction().getCustomerFranchise().getCustomerId()
                : null;

        return new RedemptionResponse(
                redemption.getId(),
                redemption.getRedemptionCode(),
                userId,
                rewardId,
                promotionId,
                redemption.getPointsUsed(),
                redemption.getStatus(),
                redemption.getExpiryDate(),
                redemption.getCreatedAt(),
                generateQR(redemption.getRedemptionCode())
        );
    }

    private static String generateQR(String code){
        return "QR-" + code;
    }
}
