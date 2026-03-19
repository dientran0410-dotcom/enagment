package service.CSFC.CSFC_auth_service.mapper;


import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;

@Component
public class PointsBalanceMapper {
    public PointsBalanceResponse toDTO(CustomerFranchise customerFranchise) {
        if (customerFranchise == null) {
            return null;
        }

        PointsBalanceResponse dto = new PointsBalanceResponse();
        dto.setCustomerId(customerFranchise.getCustomerId());
        dto.setFranchiseId(customerFranchise.getFranchiseId());
        dto.setCurrentPoints(defaultZero(customerFranchise.getCurrentPoints()));
        dto.setTotalEarnedPoints(defaultZero(customerFranchise.getTotalEarnedPoints()));
        dto.setStatus(customerFranchise.getStatus() != null ? customerFranchise.getStatus().name() : null);
        dto.setTier(toTierInfo(customerFranchise.getTier()));

        return dto;
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private PointsBalanceResponse.TierInfoDTO toTierInfo(LoyaltyTier tier) {
        if (tier == null) {
            return null;
        }

        PointsBalanceResponse.TierInfoDTO tierInfo = new PointsBalanceResponse.TierInfoDTO();
        tierInfo.setTierId(tier.getId());
        tierInfo.setTierName(tier.getName() != null ? tier.getName().name() : null);
        tierInfo.setMinPoints(defaultZero(tier.getMinPoint()));
        tierInfo.setBenefits(tier.getBenefits());
        return tierInfo;
    }
}