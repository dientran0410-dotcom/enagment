package service.CSFC.CSFC_auth_service.service;




import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApplyCouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.GenerateCouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;

import java.util.List;

public interface CouponService {

    ApplyCouponResponse applyCoupon(String code, double orderAmount);
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
    CouponCodeResponse generateQrForCoupon(String code);

}



