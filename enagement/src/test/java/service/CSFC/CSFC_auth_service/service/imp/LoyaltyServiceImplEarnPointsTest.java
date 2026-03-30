package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.PointTransaction;
import service.CSFC.CSFC_auth_service.repository.CustomerFranchiseRepository;
import service.CSFC.CSFC_auth_service.repository.LoyaltyTierRepository;
import service.CSFC.CSFC_auth_service.repository.PointTransactionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplEarnPointsTest {

    @Mock
    private CustomerFranchiseRepository customerFranchiseRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @Mock
    private LoyaltyTierRepository tierRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    private UUID customerId;
    private UUID franchiseId;
    private CustomerFranchise customerFranchise;
    private LoyaltyTier bronzeTier;
    private LoyaltyTier silverTier;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        franchiseId = UUID.randomUUID();

        // BRONZE Tier: minPoint = 0
        bronzeTier = LoyaltyTier.builder()
                .id(1L)
                .franchiseId(franchiseId)
                .name(TierName.BRONZE)
                .minPoint(0)
                .benefits("5% discount")
                .build();

        // SILVER Tier: minPoint = 1000
        silverTier = LoyaltyTier.builder()
                .id(2L)
                .franchiseId(franchiseId)
                .name(TierName.SILVER)
                .minPoint(1000)
                .benefits("10% discount")
                .build();

        customerFranchise = CustomerFranchise.builder()
                .id(1L)
                .customerId(customerId)
                .franchiseId(franchiseId)
                .currentPoints(0)
                .totalEarnedPoints(0)
                .tier(bronzeTier)
                .build();
    }

    @Test
    void earnPoints_success_shouldIncreasePoints() {
        // Given: customer có 0 points, cộng 100 points
        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(customerFranchise));

        when(tierRepository.findHighestTierByPoints(franchiseId, 100))
                .thenReturn(Optional.of(bronzeTier));

        when(customerFranchiseRepository.save(any(CustomerFranchise.class)))
                .thenReturn(customerFranchise);

        // When
        loyaltyService.earnPoints(customerId, franchiseId, 100, "ORDER123");

        // Then
        verify(customerFranchiseRepository).findByCustomerIdAndFranchiseId(customerId, franchiseId);
        verify(pointTransactionRepository).save(any(PointTransaction.class));
        verify(customerFranchiseRepository, atLeast(1)).save(any(CustomerFranchise.class));

        // Verify points were updated
        assertEquals(100, customerFranchise.getCurrentPoints());
        assertEquals(100, customerFranchise.getTotalEarnedPoints());
    }

    @Test
    void earnPoints_tierUpgrade_shouldUpdateTierToSilver() {
        // Given: customer có 900 points, cộng thêm 200 để đạt 1100 (nâng lên SILVER)
        customerFranchise.setCurrentPoints(900);
        customerFranchise.setTotalEarnedPoints(900);

        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(customerFranchise));

        when(tierRepository.findHighestTierByPoints(franchiseId, 1100))
                .thenReturn(Optional.of(silverTier));

        when(customerFranchiseRepository.save(any(CustomerFranchise.class)))
                .thenReturn(customerFranchise);

        // When
        loyaltyService.earnPoints(customerId, franchiseId, 200, "ORDER456");

        // Then
        verify(customerFranchiseRepository).findByCustomerIdAndFranchiseId(customerId, franchiseId);
        verify(pointTransactionRepository).save(any(PointTransaction.class));

        // Verify tier was updated
        verify(customerFranchiseRepository, atLeast(2)).save(any(CustomerFranchise.class));
        assertEquals(1100, customerFranchise.getCurrentPoints());
        assertEquals(1100, customerFranchise.getTotalEarnedPoints());
    }

    @Test
    void earnPoints_invalidPoints_shouldThrowException() {
        // Given: points <= 0
        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.of(customerFranchise));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            loyaltyService.earnPoints(customerId, franchiseId, 0, "INVALID");
        });
    }

    @Test
    void earnPoints_customerNotFound_shouldThrowException() {
        // Given: customer không tồn tại
        when(customerFranchiseRepository.findByCustomerIdAndFranchiseId(customerId, franchiseId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            loyaltyService.earnPoints(customerId, franchiseId, 100, "ORDER789");
        });
    }
}

