package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutCouponRequest {
    private UUID customerId;
    private String couponCode;
    private Double orderAmount;
}
