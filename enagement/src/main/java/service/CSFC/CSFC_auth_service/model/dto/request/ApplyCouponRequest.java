package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCouponRequest {
    private UUID customerId;
    private String couponCode;
    private Double orderAmount;

    public ApplyCouponRequest(String couponCode, Double orderAmount) {
        this.couponCode = couponCode;
        this.orderAmount = orderAmount;
    }
}
