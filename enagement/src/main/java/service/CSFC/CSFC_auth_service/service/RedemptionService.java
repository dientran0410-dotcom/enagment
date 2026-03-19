package service.CSFC.CSFC_auth_service.service;



import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;

import java.util.List;
import java.util.Optional;

public interface RedemptionService {
    RedemptionResponse confirmRedeem(Long rewardId) ;
    Optional<Redemption> findByRedemptionCode(String redemptionCode);
    void save(Redemption redemption);
    List<RedemptionResponse> getAll();
    RedemptionResponse findById(Long id);
}
