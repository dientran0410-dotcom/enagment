package service.CSFC.CSFC_auth_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.service.RewardService;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-service/admin/rewards")
@RequiredArgsConstructor
public class AdminRewardController {

    private final RewardService rewardService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RewardResponse>> createReward(@Valid @ModelAttribute RewardRequest request) {


        RewardResponse rewardResponse = rewardService.createReward(request,request.getImageFile());

       return ResponseEntity.ok(
                ApiResponse.success(rewardResponse, "Reward created successfully")
       );
   }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RewardResponse>> updateReward(
            @PathVariable Long id,
            @Valid @ModelAttribute RewardRequest request) {

        RewardResponse updateReward = rewardService.updateReward(id, request, request.getImageFile());

        return ResponseEntity.ok(
                ApiResponse.success(updateReward, "Reward updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> deleteReward(@PathVariable Long id) {

        rewardService.deleteReward(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Reward deleted successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> getRewardById(@PathVariable Long id) {

        RewardResponse rewardResponse = rewardService.getRewardById(id);
        return ResponseEntity.ok(
                ApiResponse.success(rewardResponse, "Reward retrieved successfully")
        );
    }

    @GetMapping("/active")
    public List<RewardResponse> getActiveRewards() {
        return rewardService.getActiveRewards();
    }

    @GetMapping
    public List<RewardResponse> getAllRewards() {
        return rewardService.getAllReward();
    }
}
