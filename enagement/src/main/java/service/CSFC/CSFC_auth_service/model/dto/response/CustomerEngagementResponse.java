package service.CSFC.CSFC_auth_service.model.dto.response;


import lombok.Builder;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.CustomerStatus;
import service.CSFC.CSFC_auth_service.model.constants.TierName;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerEngagementResponse {
    private Long id;
    private Long customerId;
    private Long franchiseId;
    private Integer currentPoints;
    private Integer totalEarnedPoints;
    private TierName tierName;
    private CustomerStatus status;
    private LocalDateTime firstOrderAt;
    private LocalDateTime lastOrderAt;
    private LocalDateTime createdAt;
}
