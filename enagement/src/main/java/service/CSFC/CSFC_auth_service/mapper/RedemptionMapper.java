package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.UUID;

@Component
public class RedemptionMapper {
    public  RedemptionResponse toResponse(Redemption redemption) {

        if (redemption == null) {
            return null;
        }

        Long rewardId = null;
        if (redemption.getReward() != null) {
            rewardId = redemption.getReward().getId();
        }

        Long promotionId = null;
        if (redemption.getPromotion() != null) {
            promotionId = redemption.getPromotion().getId();
        }

        UUID userId = null;
        if (redemption.getPointTransaction() != null &&
                redemption.getPointTransaction().getCustomerFranchise() != null) {

            userId = redemption.getPointTransaction()
                    .getCustomerFranchise()
                    .getCustomerId();
        }

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
