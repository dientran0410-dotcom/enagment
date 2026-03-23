package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.Builder;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LoyaltyRuleResponse {
    private Long id;
    private UUID franchiseId;
    private String name;
    private EventType eventType;
    private Double pointMultiplier;
    private Integer fixedPoints;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
