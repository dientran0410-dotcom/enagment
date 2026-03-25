package service.CSFC.CSFC_auth_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.GenerateCouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.service.CouponService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/engagement-service/coupons")
@Tag(name = "Admin Coupon Management", description = "APIs for managing coupons")
public class AdminCouponController {

    private final CouponService couponService;

    // ================= GET ALL =================
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(couponService.getAll(), "Get all coupons successfully")
        );
    }

    // ================= CREATE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    @Operation(
            summary = "Create Coupon",
            description = "Create a new coupon (1 coupon = 1 code)"
    )
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CouponRequest request) {

        CouponResponse response = couponService.createCoupon(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Coupon created successfully"));
    }

    // ================= BULK GENERATE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate")
    @Operation(
            summary = "Generate Bulk Coupons",
            description = "Generate multiple coupons (each coupon has 1 unique code)"
    )
    public ResponseEntity<ApiResponse<GenerateCouponResponse>> generateCoupons(
            @Valid @RequestBody GenerateCouponRequest request) {

        GenerateCouponResponse response = couponService.generateCoupons(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Generate coupons successfully"));
    }

    // ================= VALIDATE =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/validate/{code}")
    @Operation(
            summary = "Check Coupon Exists",
            description = "Check if coupon code exists in system"
    )
    public ResponseEntity<ApiResponse<Boolean>> validateCoupon(
            @PathVariable String code) {

        boolean isValid = couponService.validateCoupon(code);

        return ResponseEntity.ok(
                ApiResponse.success(isValid, "Validate coupon successfully")
        );
    }

    // ================= STATS =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/{promotionId}")
    @Operation(
            summary = "Get Coupon Statistics",
            description = "Get total number of coupons for a promotion"
    )
    public ResponseEntity<ApiResponse<GenerateCouponResponse.GenerationStats>> getStats(
            @PathVariable Long promotionId) {

        GenerateCouponResponse.GenerationStats stats =
                couponService.getGenerationStats(promotionId);

        return ResponseEntity.ok(
                ApiResponse.success(stats, "Get stats successfully")
        );
    }

    // ================= UPDATE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update Coupon")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponRequest request) {

        CouponResponse response = couponService.updateCoupon(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Update coupon successfully")
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Coupon")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {

        couponService.deleteCoupon(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Delete coupon successfully")
        );
    }
}
