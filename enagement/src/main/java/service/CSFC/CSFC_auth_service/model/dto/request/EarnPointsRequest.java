package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Data
public class EarnPointsRequest {

    private UUID customerFranchiseId;

    @NotNull(message = "Points must not be null")
    @Positive(message = "Points must be greater than 0")
    private Integer points;

    private String reason; // Optional: Lý do cộng điểm (ORDER_ID, REVIEW, etc)
}

