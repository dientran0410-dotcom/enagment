package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderCreateRequest {

    private UUID customerId;

    @NotNull(message = "Franchise ID is required")
    private UUID franchiseId;

    @NotNull(message = "Order source is required (e.g., APP, WEB)")
    private String orderSource;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    private String notes;
}