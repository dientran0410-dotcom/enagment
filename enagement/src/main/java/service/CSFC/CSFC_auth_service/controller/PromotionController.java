package service.CSFC.CSFC_auth_service.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.CreatePromotionRequest;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.service.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-service/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Controller", description = "APIs quản lý khuyến mãi (Promotions)")
public class PromotionController {

    private final PromotionService promotionService;

    @Operation(
        summary = "Tạo promotion mới",
        description = "Tạo một promotion mới với thông tin franchise, tên, mô tả, thời gian bắt đầu và kết thúc"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tạo promotion thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PostMapping("create")
    public ResponseEntity<Promotion> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        try {
            System.out.println("=== CREATE PROMOTION REQUEST ===");
            System.out.println("Request: " + request);
            Promotion newPromotion = promotionService.createPromotion(request);
            System.out.println("Created promotion: " + newPromotion);
            return ResponseEntity.status(201).body(newPromotion);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Operation(
        summary = "Lấy tất cả promotions",
        description = "Lấy danh sách tất cả các promotions không phân biệt status hay franchise"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("get-All")
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy promotions đang active",
        description = "Lấy danh sách các promotions đang active (trong khoảng thời gian hiện tại). Có thể filter theo franchiseId"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions(
            @Parameter(description = "ID của franchise (optional)", example = "1")
            @RequestParam(required = false) Long franchiseId) {
        List<Promotion> promotions = promotionService.getActivePromotions(franchiseId);
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy chi tiết promotion",
        description = "Lấy thông tin chi tiết của một promotion theo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id) {
        Promotion promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @Operation(
        summary = "Cập nhật status của promotion",
        description = "Cập nhật trạng thái của promotion (DRAFT, ACTIVE, INACTIVE, EXPIRED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Promotion> updatePromotionStatus(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Status mới", example = "ACTIVE")
            @RequestParam PromotionStatus status) {
        Promotion promotion = promotionService.updatePromotionStatus(id, status);
        return ResponseEntity.ok(promotion);
    }

    @Operation(
        summary = "Cập nhật toàn bộ promotion",
        description = "Cập nhật thông tin promotion (name, description, dates, discountType)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CreatePromotionRequest request) {
        Promotion promotion = promotionService.updatePromotion(id, request);
        return ResponseEntity.ok(promotion);
    }

    @Operation(
        summary = "Xóa promotion",
        description = "Xóa một promotion theo ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Xóa thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Lấy promotions theo franchise",
        description = "Lấy tất cả promotions của một franchise cụ thể"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/franchise/{franchiseId}")
    public ResponseEntity<List<Promotion>> getPromotionsByFranchise(
            @Parameter(description = "ID của franchise", example = "1")
            @PathVariable Long franchiseId) {
        List<Promotion> promotions = promotionService.getPromotionsByFranchise(franchiseId);
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy promotions theo status",
        description = "Lấy danh sách promotions theo trạng thái (DRAFT, ACTIVE, INACTIVE, EXPIRED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Promotion>> getPromotionsByStatus(
            @Parameter(description = "Status của promotion", example = "ACTIVE")
            @PathVariable PromotionStatus status) {
        List<Promotion> promotions = promotionService.getPromotionsByStatus(status);
        return ResponseEntity.ok(promotions);
    }

    @Operation(
        summary = "Lấy danh sách coupons của promotion",
        description = "Lấy tất cả coupons được tạo cho promotion này"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @GetMapping("/{id}/coupons")
    public ResponseEntity<?> getPromotionCoupons(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionCoupons(id));
    }

    @Operation(
        summary = "Lấy thống kê promotion",
        description = "Lấy thống kê chi tiết về promotion (số coupon, số lượt dùng, tổng giảm giá)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thống kê thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy promotion")
    })
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPromotionStats(
            @Parameter(description = "ID của promotion", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionStats(id));
    }

    @Operation(
        summary = "Dashboard tổng quan promotions",
        description = "Lấy thống kê tổng quan: tổng số promotions, active, sắp hết hạn, top promotions"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy dashboard thành công")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<?> getPromotionDashboard() {
        return ResponseEntity.ok(promotionService.getDashboard());
    }

}
