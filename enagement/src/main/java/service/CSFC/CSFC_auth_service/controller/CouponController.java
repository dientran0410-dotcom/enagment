package service.CSFC.CSFC_auth_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CheckoutCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;
import service.CSFC.CSFC_auth_service.service.CouponService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/engagement-service/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // ================= APPLY =================
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ApplyCouponResponse>> apply(
            @RequestBody ApplyCouponRequest request) {

        ApplyCouponResponse response = couponService.applyCoupon(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Áp dụng coupon thành công")
        );
    }

    // ================= QR =================
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/qr")
    public ResponseEntity<ApiResponse<CouponQrResponse>> generateQr(
            @RequestParam String code) {

        CouponQrResponse result = couponService.generateQrForCoupon(code);

        return ResponseEntity.ok(
                ApiResponse.success(result, "Generate QR successfully")
        );
    }

    // ================= ACTIVE COUPON =================
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getActiveCoupons() {

        List<CouponResponse> coupons = couponService.getActiveCouponsForCustomer();

        return ResponseEntity.ok(
                ApiResponse.success(coupons, "Active coupons retrieved successfully")
        );
    }

    // ================= Checkout COUPON =================
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<BigDecimal>> checkout(
            @RequestBody CheckoutCouponRequest request) {

        BigDecimal finalAmount = couponService.checkoutCoupon(
                request.getCustomerId(),
                request.getCouponCode(),
                request.getOrderCreateRequest()
        );

        return ResponseEntity.ok(
                ApiResponse.success(finalAmount,
                        "Thanh toán thành công, coupon đã được sử dụng")
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my-applied/{customerId}")
    public ResponseEntity<ApiResponse<List<CouponUsageResponse>>> getByCustomerId(
            @PathVariable UUID customerId) {

        List<CouponUsageResponse> result =
                couponService.getCustomerCouponUsage(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(result,"Lấy lịch sử thành công")
        );
    }
}
