package service.CSFC.CSFC_auth_service.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.config.securitymodel.UserPrincipal;
import service.CSFC.CSFC_auth_service.model.dto.request.RewardRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RewardResponse;
import service.CSFC.CSFC_auth_service.service.RewardService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/engagement-service/admin/rewards")
@RequiredArgsConstructor
public class AdminRewardController {

    private final RewardService rewardService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RewardResponse>> createReward(@Valid @ModelAttribute RewardRequest request) {


        RewardResponse rewardResponse = rewardService.createReward(request,request.getImageUrl());

       return ResponseEntity.ok(
                ApiResponse.success(rewardResponse, "Reward created successfully")
       );
   }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<RewardResponse>> updateReward(
            @PathVariable Long id,
            @Valid @ModelAttribute RewardRequest request) {

        RewardResponse updateReward = rewardService.updateReward(id, request, request.getImageUrl());

        return ResponseEntity.ok(
                ApiResponse.success(updateReward, "Reward updated successfully")
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> deleteReward(@PathVariable Long id) {

        rewardService.deleteReward(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Reward deleted successfully")
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RewardResponse>> getRewardById(@PathVariable Long id) {

        RewardResponse rewardResponse = rewardService.getRewardById(id);
        return ResponseEntity.ok(
                ApiResponse.success(rewardResponse, "Reward retrieved successfully")
        );
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/active")
    public List<RewardResponse> getActiveRewards(Authentication authentication) {

        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) authentication;

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        assert principal != null;
        UUID customerId = UUID.fromString(principal.getUserId());

        return rewardService.getActiveRewards(customerId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public List<RewardResponse> getAllRewards() {
        return rewardService.getAllReward();
    }
}
