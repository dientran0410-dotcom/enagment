package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemAddonRequest {
    @NotNull(message = "Addon ID is required")
    private UUID addonId;

    @NotNull(message = "Addon quantity is required")
    @Min(value = 1, message = "Addon quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Addon price is required")
    @Min(value = 0, message = "Addon price must be greater than or equal to 0")
    private Double price;
}