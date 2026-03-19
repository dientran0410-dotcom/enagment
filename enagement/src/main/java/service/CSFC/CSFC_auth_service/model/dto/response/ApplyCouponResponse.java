package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyCouponResponse {
    private Double orderAmount;
    private Double discountAmount;
    private Double finalAmount;
}
