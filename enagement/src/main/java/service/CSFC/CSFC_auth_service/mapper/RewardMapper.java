package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.Reward;

@Component
public class RewardMapper {
    public RewardResponse toResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .franchiseId(reward.getFranchiseId())
                .name(reward.getName())
                .requiredPoints(reward.getRequiredPoints())
                .description(reward.getDescription())
                .active(reward.getIsActive())
                .imageUrl(reward.getImageUrl())
                .build();
    }
}
