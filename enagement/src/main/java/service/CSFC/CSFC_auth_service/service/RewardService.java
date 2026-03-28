package service.CSFC.CSFC_auth_service.service;


import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RewardService {
    List<RewardResponse> getAllReward();
    List<RewardResponse> getActiveRewards(UUID customerId);
    RewardResponse createReward(RewardRequest request, MultipartFile file);
    RewardResponse updateReward(Long rewardId, RewardRequest request, MultipartFile file);
    void deleteReward(Long rewardId);
    RewardResponse getRewardById(Long rewardId);
}
