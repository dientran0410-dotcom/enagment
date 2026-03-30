package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPaymentRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Franchise ID is required")
    private UUID franchiseId;

    private String invoiceId; // Optional invoice reference

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be greater than 0")
    private Double subtotal;

    private Double shippingFee = 0.0;

    private Double taxAmount = 0.0;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be greater than 0")
    private Double totalAmount;

    private Integer pointsDiscount = 0;

    private String couponCode; // Optional coupon to apply

    private String orderSource = "APP"; // APP, WEB, etc.

    private String notes;
}

