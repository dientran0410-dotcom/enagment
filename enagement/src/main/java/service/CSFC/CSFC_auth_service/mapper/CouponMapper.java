package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;

@Component
public class CouponMapper {

    public CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .promotionId(coupon.getPromotion().getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .userLimit(coupon.getUserLimit())
                .usedCount(coupon.getUsedCount())
                .minTier(coupon.getMinTier())
                .isPublic(coupon.getIsPublic())
                .createdAt(coupon.getCreatedAt())
                .expiredAt(coupon.getExpiredAt())
                .build();
    }

    public Coupon toEntity(CouponRequest couponRequest) {

        Promotion promotion = null;
        if (couponRequest.getPromotionId() != null) {
            promotion = new Promotion();
            promotion.setId(couponRequest.getPromotionId());
        }else{
            throw new IllegalArgumentException("Promotion ID must not be null");
        }

        LoyaltyTier tier = null;
        if (couponRequest.getMinTierId() != null) {
            tier = new LoyaltyTier();
            tier.setId(couponRequest.getMinTierId());
        }
        else {

        }

        return Coupon.builder()
                .promotion(promotion)
                .code(couponRequest.getCode())
                .discountType(couponRequest.getDiscountType())
                .discountValue(couponRequest.getDiscountValue())
                .minOrderValue(couponRequest.getMinOrderValue())
                .maxDiscount(couponRequest.getMaxDiscount())
                .usageLimit(couponRequest.getUsageLimit())
                .userLimit(couponRequest.getUserLimit())
                .minTier(tier)
                .isPublic(couponRequest.getIsPublic())
                .build();

    }

    public void updateEntity(Coupon coupon, CouponRequest request) {

        coupon.setCode(request.getCode());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUserLimit(request.getUserLimit());
        coupon.setIsPublic(request.getIsPublic());
    }
}
