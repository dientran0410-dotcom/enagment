package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.CSFC.CSFC_auth_service.model.constants.RedemptionStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedemptionResponse {
    private Long id;
    private  String redemptionCode;
    private Long userId;
    private Long rewardId;
    private Long promotionId;
    private Integer pointsUsed;
    private RedemptionStatus status;
    private LocalDateTime expirationDate;
    private LocalDateTime creationDate;
    private String qrImage;
}
