package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class RedeemResponse {
    private String redemptionCode;
    private Integer pointUsed;
    private Integer currentPoints;
}
