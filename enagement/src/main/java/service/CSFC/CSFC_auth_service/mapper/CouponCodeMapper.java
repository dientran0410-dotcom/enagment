package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import service.CSFC.CSFC_auth_service.model.entity.CouponCode;

@Component
public class CouponCodeMapper {
    public CouponCodeResponse toResponse(CouponCode couponCode){
        return CouponCodeResponse.builder()
                .code(couponCode.getCode())
                .redeemUrl(couponCode.getRedeemUrl())
                .discountValue(couponCode.getCoupon().getDiscountValue())
                .build();
    }
}
