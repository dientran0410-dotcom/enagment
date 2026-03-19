package service.CSFC.CSFC_auth_service.service.imp;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.CSFC.CSFC_auth_service.mapper.RewardMapper;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.RewardRepository;
import service.CSFC.CSFC_auth_service.service.FileStorageService;
import service.CSFC.CSFC_auth_service.service.RewardService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;

    private final FileStorageService fileStorageService;

    private final RewardMapper rewardMapper;

    @Override
    public List<RewardResponse> getAllReward() {
        List<Reward> rewards = rewardRepository.findAll();

        return rewards.stream()
                .map(rewardMapper::toResponse)
                .toList();
    }

    @Override
    public List<RewardResponse> getActiveRewards() {
        List<Reward> rewards = rewardRepository.findByIsActiveTrue();

        return rewards.stream()
                .map(rewardMapper::toResponse)
                .toList();
    }

    @Override
    public RewardResponse createReward(RewardRequest request, MultipartFile imageFile) {

        String imgUrl = null;

        if(imageFile != null && !imageFile.isEmpty()){
            imgUrl = fileStorageService.saveImage(imageFile);
        }

        Reward reward = new Reward();
        reward.setFranchiseId(request.getFranchiseId());
        reward.setName(request.getName());
        reward.setRequiredPoints(request.getRequiredPoints());
        reward.setDescription(request.getDescription());
        reward.setIsActive(request.getActive());
        reward.setImageUrl(imgUrl);

        Reward savedReward = rewardRepository.save(reward);


        return rewardMapper.toResponse(savedReward);
    }

    @Override
    public RewardResponse updateReward(Long rewardId, RewardRequest request, MultipartFile imageFile) {
        Reward existingReward = rewardRepository.findById(rewardId).
                orElseThrow(() -> new RuntimeException("Reward not found with id: " + rewardId));

        String imgUrl = fileStorageService.updateImage(existingReward.getImageUrl(), imageFile);
        existingReward.setFranchiseId(request.getFranchiseId());
        existingReward.setName(request.getName());
        existingReward.setRequiredPoints(request.getRequiredPoints());
        existingReward.setDescription(request.getDescription());
        existingReward.setIsActive(request.getActive());
        existingReward.setImageUrl(imgUrl);

        Reward savedReward = rewardRepository.save(existingReward);

        return rewardMapper.toResponse(savedReward);
    }

    @Override
    public void deleteReward(Long rewardId) {
        Reward existingReward = rewardRepository.findById(rewardId).
                orElseThrow(() -> new RuntimeException("Reward not found with id: " + rewardId));
        fileStorageService.deleteImage(existingReward.getImageUrl());
        rewardRepository.delete(existingReward);
    }

    @Override
    public RewardResponse getRewardById(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId).
                orElseThrow(() -> new RuntimeException("Reward not found with id: " + rewardId));

        return rewardMapper.toResponse(reward);
    }
}
