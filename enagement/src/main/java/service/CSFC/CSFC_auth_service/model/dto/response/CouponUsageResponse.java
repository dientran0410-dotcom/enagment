package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponUsageResponse {

    private Long id;

    private UUID customerId;
    private Long couponId;
    private String couponCode;
    private DiscountType discountType;
    private Double discountValue;
    private String status;
    private LocalDateTime createdAt;
}
