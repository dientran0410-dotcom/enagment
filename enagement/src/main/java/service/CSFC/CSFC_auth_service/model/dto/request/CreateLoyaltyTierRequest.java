package service.CSFC.CSFC_auth_service.model.dto.request;


import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.TierName;

@Data
public class CreateLoyaltyTierRequest {
    private Long franchiseId;
    private TierName name;
    private String benefits;
    private Integer minPoint;
}
