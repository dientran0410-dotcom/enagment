package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class RedeemRequest {

    private UUID customerFranchiseId;
    private Long rewardId;
}
