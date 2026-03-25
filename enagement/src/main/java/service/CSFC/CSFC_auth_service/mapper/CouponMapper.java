package service.CSFC.CSFC_auth_service.mapper;

import org.springframework.stereotype.Component;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponUsageResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.CouponUsage;
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
                .startAt(coupon.getStartAt())
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
            throw new IllegalArgumentException("Tier ID must not be null");
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
                .createdAt(couponRequest.getStartAt())
                .expiredAt(couponRequest.getExpiredAt())
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
        // Map expired field when provided in the request
        if (request.getExpiredAt() != null) {
            coupon.setExpiredAt(request.getExpiredAt());
        }
        if (request.getStartAt() != null) {
            coupon.setStartAt(request.getStartAt());
        }
    }

    public CouponUsageResponse mapToResponse(CouponUsage usage) {
        Coupon coupon = usage.getCoupon();

        return CouponUsageResponse.builder()
                .id(usage.getId())
                .customerId(usage.getCustomerId())
                .couponId(coupon.getId())
                .couponCode(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .status(usage.getStatus().name())
                .createdAt(usage.getCreatedAt())
                .build();
    }
}
