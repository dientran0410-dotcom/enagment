package service.CSFC.CSFC_auth_service.model.dto.response;


import lombok.Builder;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.TierName;

@Data
@Builder
public class LoyaltyTierResponse {

    private Long id;
    private Long franchiseId;
    private TierName name;
    private Integer minPoint;
    private String benefits;
}