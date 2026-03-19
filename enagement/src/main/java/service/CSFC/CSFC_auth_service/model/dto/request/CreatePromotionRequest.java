package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;

import java.time.LocalDateTime;

@Data
public class CreatePromotionRequest {
    @NotNull(message = "Franchise ID is required")
    private Long franchiseId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    
    private String discountType; // VD: PERCENT, FIXED

    private PromotionStatus status;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}