package service.CSFC.CSFC_auth_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.request.RedeemRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.CustomerEngagementResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.RedeemResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.TransactionHistoryResponse;
import service.CSFC.CSFC_auth_service.service.LoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-service/loyalty")
@RequiredArgsConstructor
public class CustomerLoyaltyController {
    private final LoyaltyService loyaltyService;

    @PreAuthorize("hasAnyAuthority('CUSTOMER','STAFF','ADMIN')")
    @GetMapping("/customers/{customerId}/franchise/{franchiseId}")
    public ResponseEntity<CustomerEngagementResponse> getCustomerEngagement(
            @PathVariable Long customerId,
            @PathVariable Long franchiseId) {
//        if (!currentUserId.equals(customerId)) {
//            throw new AccessDeniedException("Không được xem dữ liệu người khác");
//        }
        CustomerEngagementResponse response = loyaltyService.getCustomerEngagement(customerId, franchiseId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('CUSTOMER','STAFF','ADMIN')")
    @GetMapping("/customers/{customerId}/franchise/{franchiseId}/transactions")
    public ResponseEntity<List<TransactionHistoryResponse>> getTransactionHistory(
            @PathVariable Long customerId,
            @PathVariable Long franchiseId) {
        //        if (!currentUserId.equals(customerId)) {
//            throw new AccessDeniedException("Không được xem dữ liệu người khác");
//        }
        List<TransactionHistoryResponse> transactions = loyaltyService.getTransactionHistory(customerId, franchiseId);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PostMapping("/redeem")
    public ResponseEntity<RedeemResponse> redeem(
            @RequestBody RedeemRequest request
    ) {
        RedeemResponse response = loyaltyService.redeem(request);
        return ResponseEntity.ok(response);
    }
}
