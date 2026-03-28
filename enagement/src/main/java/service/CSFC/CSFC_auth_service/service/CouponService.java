package service.CSFC.CSFC_auth_service.service;

import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.OrderCreateRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.CouponUsage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {

    ApplyCouponResponse applyCoupon(ApplyCouponRequest request);
    /**
     * Generate bulk coupon codes for a promotion
     * High performance bulk insert with duplicate checking
     */
    GenerateCouponResponse generateCoupons(GenerateCouponRequest request);

    //Validate coupon code
    boolean validateCoupon(String code);

    //Get statistics about coupon generatio
    GenerateCouponResponse.GenerationStats getGenerationStats(Long promotionId);

    List<Coupon> getAll();
    void deleteCoupon(Long id);
    CouponResponse updateCoupon(Long id, CouponRequest coupon);
    CouponResponse createCoupon(CouponRequest request);
    CouponQrResponse generateQrForCoupon(String code);


    List<CouponResponse> getActiveCouponsForCustomer(UUID customerId);
    BigDecimal checkoutCoupon(UUID customerId, String couponCode, OrderCreateRequest orderCreateRequest);
    List<CouponUsageResponse> getCustomerCouponUsage(UUID customerId);
}



