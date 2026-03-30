package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Data
public class PaymentCheckoutRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Franchise ID is required")
    private UUID franchiseId;

    @NotNull(message = "Order total amount is required")
    @Positive(message = "Order amount must be greater than 0")
    private Double orderAmount; // Giá tiền của đơn hàng

    private String orderId; // Optional: Order ID reference
}

