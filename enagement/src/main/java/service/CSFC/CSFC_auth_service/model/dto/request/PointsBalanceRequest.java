package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PointsBalanceRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Franchise ID is required")
    private Long franchiseId;
}

