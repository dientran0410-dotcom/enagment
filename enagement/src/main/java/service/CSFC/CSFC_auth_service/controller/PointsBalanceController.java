package service.CSFC.CSFC_auth_service.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.common.config.securitymodel.UserPrincipal;
import service.CSFC.CSFC_auth_service.model.dto.request.PointsBalanceRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.PointsBalanceResponse;
import service.CSFC.CSFC_auth_service.service.PointsBalanceService;

import java.util.UUID;

@RestController
@RequestMapping("/api/engagement-service/points")
@Tag(name = "Points Balance", description = "API for viewing customer points balance")
@RequiredArgsConstructor
public class PointsBalanceController {


    private final PointsBalanceService pointsBalanceService;

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Extract userId from UserPrincipal object (set by HeaderAuthenticationFilter)
        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new AccessDeniedException("Invalid principal type");
        }

        String userId = ((UserPrincipal) principal).getUserId();
        if (userId == null || userId.isBlank()) {
            throw new AccessDeniedException("User ID is missing");
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Invalid user ID format: " + userId);
        }
    }

    private boolean isCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a != null && a.getAuthority() != null && a.getAuthority().equals("ROLE_CUSTOMER"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/balance")
    @Operation(summary = "Get Points Balance (Query Params)",
               description = "Retrieve current points balance and tier information for a customer at a specific franchise using query parameters")
    public ResponseEntity<PointsBalanceResponse> getPointsBalance(
            @RequestParam UUID customerId,
            @RequestParam UUID franchiseId) {

        if (isCustomer()) {
            UUID currentUserId = getCurrentUserId();
            if (!currentUserId.equals(customerId)) {
                throw new AccessDeniedException("Forbidden");
            }
        }

        PointsBalanceResponse pointsBalance =
                pointsBalanceService.getPointsBalance(customerId, franchiseId);

        if (pointsBalance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pointsBalance);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @PostMapping("/balance")
    @Operation(summary = "Get Points Balance (Request Body)",
               description = "Retrieve current points balance and tier information for a customer at a specific franchise using request body")
    public ResponseEntity<PointsBalanceResponse> getPointsBalanceByRequest(
            @Valid @RequestBody PointsBalanceRequest request) {

        if (isCustomer()) {
            UUID currentUserId = getCurrentUserId();
            if (!currentUserId.equals(request.getCustomerId())) {
                throw new AccessDeniedException("Forbidden");
            }
        }

        PointsBalanceResponse pointsBalance =
                pointsBalanceService.getPointsBalance(
                        request.getCustomerId(),
                        request.getFranchiseId()
                );

        if (pointsBalance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pointsBalance);
    }
}
