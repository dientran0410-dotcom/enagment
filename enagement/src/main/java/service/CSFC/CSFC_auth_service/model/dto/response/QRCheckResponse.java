package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class QRCheckResponse {
    private boolean valid;
    private String message;
    private String redemptionCode;
    private Integer redemptionPoints;
    private String rewardName;

}
