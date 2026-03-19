package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import service.CSFC.CSFC_auth_service.model.constants.EventType;

import java.time.LocalDateTime;


@Getter
@Setter
@Data
public class LoyaltyRuleRequest {
    private String name;
    private EventType eventType;
    private Double pointMultiplier;
    private Integer fixedPoints;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
