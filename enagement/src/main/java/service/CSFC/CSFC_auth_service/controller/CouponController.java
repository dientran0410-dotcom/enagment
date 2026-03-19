package service.CSFC.CSFC_auth_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.ApplyCouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;
import service.CSFC.CSFC_auth_service.service.CouponService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/engagement-service/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponCodeGeneratorService couponCodeGeneratorService;

    @PostMapping("/apply")
    public ApiResponse<ApplyCouponResponse> apply(@RequestBody ApplyCouponRequest req){
        ApplyCouponResponse result = couponService.applyCoupon(
                req.getCouponCode(),
                req.getOrderAmount()
        );
        return ApiResponse.success(
                result,
                "Áp dụng phiếu giảm giá thành công"
        );
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAll()
    {

        return ResponseEntity.ok(
                ApiResponse.success(couponService.getAll(),"Get all coupon successfully")
        );
    }
    @PostMapping("create")
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Coupon created successfully")
        );
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<ApiResponse<CouponCodeResponse>> generateQr(
            @RequestParam String code) {

        CouponCodeResponse result = couponService.generateQrForCoupon(code);

        return ResponseEntity.ok(
                ApiResponse.success(
                        result,
                        "Tạo QR (redeem URL) thành công"
                )
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponCodeResponse>> getCouponByCode(@PathVariable String code) {
        CouponCodeResponse result = couponCodeGeneratorService.getCouponCode(code);
        return  ResponseEntity.ok(
                ApiResponse.success(result,"Coupon code successfully")
        );
    }

}
