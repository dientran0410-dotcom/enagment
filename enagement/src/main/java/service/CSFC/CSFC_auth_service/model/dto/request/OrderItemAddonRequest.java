package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemAddonRequest {
    @NotNull(message = "Addon Variant ID is required")
    private UUID addonVariantId;

    @NotNull(message = "Addon quantity is required")
    @Min(value = 1, message = "Addon quantity must be at least 1")
    private Integer quantity;
}