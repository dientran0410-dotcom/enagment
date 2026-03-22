package service.CSFC.CSFC_auth_service.service;



import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RedemptionService {
    RedemptionResponse confirmRedeem(Long rewardId, UUID customerIdInput) ;
    Optional<Redemption> findByRedemptionCode(String redemptionCode);
    void save(Redemption redemption);
    List<RedemptionResponse> getAll();
    RedemptionResponse findById(Long id);
    List<RedemptionResponse> getByUserId(UUID userId);
}
