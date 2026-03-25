package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreatePromotionRequest {
    @NotNull(message = "Franchise ID is required")
    private UUID franchiseId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    
    private DiscountType discountType; // VD: PERCENT, FIXED

    private PromotionStatus status;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}