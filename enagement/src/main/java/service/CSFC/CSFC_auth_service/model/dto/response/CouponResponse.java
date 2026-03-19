package service.CSFC.CSFC_auth_service.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private Long id;
    private Long promotionId;
    private String code;
    private DiscountType discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private Integer usageLimit;
    private Integer userLimit;
    private Integer usedCount;
    private LoyaltyTier minTier;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
