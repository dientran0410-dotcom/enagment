package service.CSFC.CSFC_auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CustomerEngagementResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RedeemResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.TransactionHistoryResponse;
import service.CSFC.CSFC_auth_service.service.LoyaltyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/engagement-service/loyalty")
@RequiredArgsConstructor
public class CustomerLoyaltyController {

    private final LoyaltyService loyaltyService;
    private final HttpServletRequest httpServletRequest;

    // ================= HELPER =================
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        return UUID.fromString(auth.getName());
    }

    private boolean isCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
    }

    // ================= CUSTOMER ENGAGEMENT =================
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/customers/{customerId}/franchise/{franchiseId}")
    public ResponseEntity<CustomerEngagementResponse> getCustomerEngagement(
            @PathVariable UUID customerId,
            @PathVariable UUID franchiseId) {

        if (isCustomer()) {
            UUID currentUserId = getCurrentUserId();
            if (!currentUserId.equals(customerId)) {
                throw new AccessDeniedException("Không được xem dữ liệu người khác");
            }
        }

        CustomerEngagementResponse response =
                loyaltyService.getCustomerEngagement(customerId, franchiseId);

        return ResponseEntity.ok(response);
    }

    // ================= TRANSACTION HISTORY =================
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/customers/{customerId}/franchise/{franchiseId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable UUID customerId,
            @PathVariable UUID franchiseId) {

        if (isCustomer()) {
            UUID currentUserId = getCurrentUserId();
            if (!currentUserId.equals(customerId)) {
                throw new AccessDeniedException("Không được xem dữ liệu người khác");
            }
        }

        List<TransactionHistoryResponse> transactions =
                loyaltyService.getTransactionHistory(customerId, franchiseId);

        return ResponseEntity.ok(transactions);
    }

    // ================= CUSTOMER REGISTRATION =================
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/register/{franchiseId}")
    public ResponseEntity<ApiResponse<CustomerEngagementResponse>> registerCustomer(
            @PathVariable UUID franchiseId) {

        UUID customerId = getCurrentUserId();
        String jwtToken = getJwtToken();

        CustomerEngagementResponse response =
                loyaltyService.registerCustomer(customerId, franchiseId, jwtToken);

        return ResponseEntity.status(201).body(
                ApiResponse.success(response, "Customer registered successfully for franchise")
        );
    }

    // ================= REDEEM =================
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/redeem")
    public ResponseEntity<RedeemResponse> redeem(@RequestBody RedeemRequest request) {

        UUID customerId = getCurrentUserId();

        RedeemResponse response =
                loyaltyService.redeem(request, customerId);

        return ResponseEntity.ok(response);
    }

    // ================= HELPER METHODS =================
    private String getJwtToken() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String) {
            String credentials = (String) auth.getCredentials();
            if (credentials != null && !credentials.isBlank() && !"[PROTECTED]".equalsIgnoreCase(credentials)) {
                return credentials;
            }
        }
        throw new AccessDeniedException("Missing or invalid JWT token");
    }
}