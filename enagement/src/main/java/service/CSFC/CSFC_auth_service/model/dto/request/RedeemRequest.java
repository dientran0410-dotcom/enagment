package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;

@Data
public class RedeemRequest {

    private Long customerFranchiseId;
    private Long rewardId;
}
