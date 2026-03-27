package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.*;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRequest {

    private Long promotionId;

    private String code;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscount;

    private Integer usageLimit;

    private Integer userLimit;

    private Long minTierId;

    private Boolean isPublic;

    private LocalDateTime startAt;

    private LocalDateTime expiredAt;
}