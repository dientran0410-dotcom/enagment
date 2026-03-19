package service.CSFC.CSFC_auth_service.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.dto.request.CreateLoyaltyTierRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.LoyaltyRuleRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.CustomerEngagementResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.LoyaltyRuleResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.LoyaltyTierResponse;
import service.CSFC.CSFC_auth_service.service.LoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-service/admin/loyalty")
@RequiredArgsConstructor
public class AdminLoyaltyController {
    private final LoyaltyService loyaltyService;

    @Operation(summary = "Get all tiers")
    @GetMapping("/tiers")
    public ResponseEntity<List<LoyaltyTierResponse>> getAllTiers() {
        return ResponseEntity.ok(loyaltyService.getAllTiers());
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerEngagementResponse>> getAllCustomers(
            @Parameter(description = "Filter by franchise ID") @RequestParam(required = false) Long franchiseId,
            @Parameter(description = "Filter by tier ID") @RequestParam(required = false) Long tierId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CustomerEngagementResponse> customers = loyaltyService.getAllCustomers(franchiseId, tierId, pageable);
        return ResponseEntity.ok(customers);
    }

    @Operation(
            summary = "Tạo Loyalty Tier cố định",
            description = """
            Hệ thống Loyalty sử dụng 3 cấp bậc cố định:

            - BRONZE  → minPoints = 0
            - SILVER  → minPoints = 500
            - GOLD    → minPoints = 1000
            - PLATINUM -> minPoints = 2000

            Lưu ý:
            - minPoints được hệ thống tự động thiết lập theo name.
            - Admin chỉ cần truyền: franchiseId, name, tierMultiplier, benefits.
            - Không được tạo trùng tier trong cùng franchise.
            """
    )
    @PostMapping("/tiers")
    public ResponseEntity<LoyaltyTierResponse> createTier(
            @RequestBody CreateLoyaltyTierRequest request) {
        LoyaltyTierResponse response = loyaltyService.createTier(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update tier theo franchise id và tier name")
    @PutMapping("/{franchiseId}/{tierName}")
    public ResponseEntity<LoyaltyTierResponse> updateTier(
            @PathVariable Long franchiseId,
            @PathVariable TierName tierName,
            @RequestBody CreateLoyaltyTierRequest request) {
        return ResponseEntity.ok(
                loyaltyService.updateTier(franchiseId, tierName, request)
        );
    }

    @Operation(summary = "Delete tier")
    @DeleteMapping("/tiers")
    public ResponseEntity<Void> deleteTier(
            @RequestParam Long franchiseId,
            @RequestParam TierName tierName) {
        loyaltyService.deleteTier(franchiseId, tierName);
        return ResponseEntity.noContent().build();
    }

    // ========== Loyalty Rules ================

    @Operation(summary = "Create rule")
    @PostMapping("/franchises/{franchiseId}/rules")
    public ResponseEntity<LoyaltyRuleResponse> createRule(
            @PathVariable Long franchiseId,
            @RequestBody LoyaltyRuleRequest request) {
        return ResponseEntity.ok(loyaltyService.createRule(franchiseId, request));
    }

    @Operation(summary = "List all rules across all franchises")
    @GetMapping("/rules")
    public ResponseEntity<List<LoyaltyRuleResponse>> getAllRules() {
        return ResponseEntity.ok(loyaltyService.getAllRules());
    }

    @Operation(summary = "Update rule by franchise and event type")
    @PutMapping("/franchises/{franchiseId}/rules/event/{eventType}")
    public ResponseEntity<LoyaltyRuleResponse> updateRule(
            @PathVariable Long franchiseId,
            @PathVariable EventType eventType,
            @RequestBody LoyaltyRuleRequest request) {
        return ResponseEntity.ok(loyaltyService.updateRule(franchiseId, eventType, request));
    }

    @Operation(summary = "Delete rule")
    @DeleteMapping("/franchises/{franchiseId}/rules/event/{eventType}")
    public ResponseEntity<Void> deleteRule(
            @PathVariable Long franchiseId,
            @PathVariable EventType eventType) {
        loyaltyService.deleteRule(franchiseId, eventType);
        return ResponseEntity.noContent().build();
    }
}
