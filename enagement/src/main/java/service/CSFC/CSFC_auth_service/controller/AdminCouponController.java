package service.CSFC.CSFC_auth_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.GenerateCouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.service.CouponService;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/engagement-service/coupons")
@Tag(name = "Admin Coupon Management", description = "APIs for generating and managing coupon codes")
public class AdminCouponController {


    private final CouponService couponService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate Bulk Coupon Codes",
            description = "Generate multiple unique coupon codes for a promotion. Supports bulk insert up to 10,000 codes with high performance and duplicate checking."
    )
    public ResponseEntity<GenerateCouponResponse> generateCoupons(
            @Valid @RequestBody GenerateCouponRequest request) {
        try {
            GenerateCouponResponse response = couponService.generateCoupons(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            GenerateCouponResponse errorResponse = new GenerateCouponResponse();
            errorResponse.setMessage("Invalid request: " + e.getMessage());
            errorResponse.setSuccessCount(0);
            errorResponse.setFailedCount(request.getQuantity());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            GenerateCouponResponse errorResponse = new GenerateCouponResponse();
            errorResponse.setMessage("Error generating coupons: " + e.getMessage());
            errorResponse.setSuccessCount(0);
            errorResponse.setFailedCount(request.getQuantity());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/validate/{code}")
    @Operation(
            summary = "Validate Coupon Code",
            description = "Check if a coupon code exists and is valid"
    )
    public ResponseEntity<Boolean> validateCoupon(
            @Parameter(description = "Coupon code to validate", required = true)
            @PathVariable String code) {
        boolean isValid = couponService.validateCoupon(code);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/stats/{promotionId}")
    @Operation(
            summary = "Get Coupon Statistics",
            description = "Retrieve total number of generated coupons for a specific promotion. " +
                    "Note: Execution time and performance metrics are only available in the " +
                    "response of the generate endpoint, not stored in database."
    )
    public ResponseEntity<GenerateCouponResponse.GenerationStats> getStats(
            @Parameter(description = "Promotion ID", required = true)
            @PathVariable Long promotionId) {
        GenerateCouponResponse.GenerationStats stats = couponService.getGenerationStats(promotionId);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponRequest request) {

        CouponResponse response = couponService.updateCoupon(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(response,"Cập nhật coupon thành công")
        );
    }

    @DeleteMapping("/{id}")
    public void deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
    }
}
