package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CSFC.CSFC_auth_service.common.exception.InsufficientPointsException;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.mapper.RedemptionMapper;
import service.CSFC.CSFC_auth_service.model.constants.EventType;
import service.CSFC.CSFC_auth_service.model.dto.response.RedemptionResponse;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyRule;
import service.CSFC.CSFC_auth_service.model.entity.Redemption;
import service.CSFC.CSFC_auth_service.model.entity.Reward;
import service.CSFC.CSFC_auth_service.repository.*;
import service.CSFC.CSFC_auth_service.service.QrCodeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceImplTest {

    @Mock private QrCodeService qrCodeService;
    @Mock private RewardRepository rewardRepository;
    @Mock private CustomerFranchiseRepository customerFranchiseRepository;
    @Mock private RedemptionRepository redemptionRepository;
    @Mock private PointTransactionRepository pointTransactionRepository;
    @Mock private LoyaltyRuleRepository loyaltyRuleRepository;
    @Mock private RedemptionMapper redemptionMapper;

    @InjectMocks
    private RedemptionServiceImpl redemptionService;

    private UUID userId;
    private Reward reward;
    private CustomerFranchise customer;
    private LoyaltyRule rule;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        reward = new Reward();
        reward.setId(1L);
        reward.setRequiredPoints(100);
        reward.setIsActive(true);

        customer = new CustomerFranchise();
        customer.setCustomerId(userId);
        customer.setCurrentPoints(200);

        rule = new LoyaltyRule();
        rule.setEndDate(LocalDateTime.now().plusDays(7));
        rule.setPointMultiplier(1.0);
    }

    // ==================== HAPPY PATH ====================
    @Test
    void confirmRedeem_success_fullVerification() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId)).thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION))
                .thenReturn(Optional.of(rule));
        when(qrCodeService.generateQrBase64(any())).thenReturn("QR-BASE64");
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        RedemptionResponse res = redemptionService.confirmRedeem(1L, userId);

        // === Verify response ===
        assertNotNull(res);
        assertEquals(10L, res.getId());
        assertEquals("QR-BASE64", res.getQrImage());
        assertNotNull(res.getCreationDate());

        // === Verify customer points ===
        assertEquals(100, customer.getCurrentPoints()); // 200 - 100

        // === Verify repositories interactions ===
        verify(customerFranchiseRepository).save(customer);
        verify(redemptionRepository).save(any());
        verify(qrCodeService).generateQrBase64(any());
        // Chỉ verify 1 lần vì service hiện tại chỉ tạo 1 transaction
        verify(pointTransactionRepository, times(1)).save(any());
    }

    // ==================== INVALID INPUT ====================
    @Test
    void confirmRedeem_userIdNull_throwException() {
        assertThrows(IllegalArgumentException.class,
                () -> redemptionService.confirmRedeem(1L, null));
    }

    // ==================== RESOURCE NOT FOUND ====================
    @Test
    void confirmRedeem_rewardNotFound_throwException() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> redemptionService.confirmRedeem(1L, userId));
    }

    @Test
    void confirmRedeem_rewardInactive_throwException() {
        reward.setIsActive(false);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        assertThrows(ResourceNotFoundException.class,
                () -> redemptionService.confirmRedeem(1L, userId));
    }

    @Test
    void confirmRedeem_notEnoughPoints_throwException() {
        customer.setCurrentPoints(50);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId)).thenReturn(Optional.of(customer));
        assertThrows(InsufficientPointsException.class,
                () -> redemptionService.confirmRedeem(1L, userId));
    }

    @Test
    void confirmRedeem_ruleNotFound_throwException() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId)).thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> redemptionService.confirmRedeem(1L, userId));
    }

    // ==================== EDGE CASE ====================
    @Test
    void confirmRedeem_exactPoints_shouldSucceed() {
        customer.setCurrentPoints(100);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId)).thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION)).thenReturn(Optional.of(rule));
        when(qrCodeService.generateQrBase64(any())).thenReturn("QR-BASE64");
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(20L);
            return r;
        });

        RedemptionResponse res = redemptionService.confirmRedeem(1L, userId);

        assertEquals(0, customer.getCurrentPoints());
        assertEquals(20L, res.getId());
        assertEquals("QR-BASE64", res.getQrImage());
        verify(pointTransactionRepository, times(1)).save(any());
    }

    @Test
    void confirmRedeem_rewardPointsZero_shouldNotDeduct() {
        reward.setRequiredPoints(0);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId)).thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION)).thenReturn(Optional.of(rule));
        when(qrCodeService.generateQrBase64(any())).thenReturn("QR-BASE64");
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(30L);
            return r;
        });

        RedemptionResponse res = redemptionService.confirmRedeem(1L, userId);

        assertEquals(200, customer.getCurrentPoints()); // Không bị trừ
        assertEquals(30L, res.getId());
        verify(pointTransactionRepository, times(1)).save(any());
    }

    // ========= STATUS & BALANCE BOUNDARY TESTS ==========
    @Test
    void confirmRedeem_exactPointsBalance_success() {
        UUID userId = UUID.randomUUID();
        CustomerFranchise customer = new CustomerFranchise();
        customer.setCurrentPoints(100);

        Reward reward = new Reward();
        reward.setId(1L);
        reward.setRequiredPoints(100);
        reward.setIsActive(true);

        LoyaltyRule rule = new LoyaltyRule();

        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId))
                .thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION))
                .thenReturn(Optional.of(rule));
        when(customerFranchiseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pointTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        RedemptionResponse res = redemptionService.confirmRedeem(1L, userId);

        assertEquals(0, customer.getCurrentPoints());
        assertEquals(100, res.getPointsUsed());
        verify(pointTransactionRepository).save(any());
    }

    @Test
    void confirmRedeem_insufficientPoints_throwException() {
        UUID userId = UUID.randomUUID();
        CustomerFranchise customer = new CustomerFranchise();
        customer.setCurrentPoints(50);

        Reward reward = new Reward();
        reward.setId(1L);
        reward.setRequiredPoints(100);
        reward.setIsActive(true);

        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId))
                .thenReturn(Optional.of(customer));

        assertThrows(Exception.class, () -> redemptionService.confirmRedeem(1L, userId));
        verify(customerFranchiseRepository, never()).save(any());
    }

    @Test
    void confirmRedeem_nullUserId_throwException() {
        assertThrows(Exception.class, () -> redemptionService.confirmRedeem(1L, null));
    }

    @Test
    void confirmRedeem_pointsBoundary_onePointExtra_success() {
        UUID userId = UUID.randomUUID();
        CustomerFranchise customer = new CustomerFranchise();
        customer.setCurrentPoints(101);

        Reward reward = new Reward();
        reward.setId(1L);
        reward.setRequiredPoints(100);
        reward.setIsActive(true);

        LoyaltyRule rule = new LoyaltyRule();

        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(customerFranchiseRepository.findByCustomerId(userId))
                .thenReturn(Optional.of(customer));
        when(loyaltyRuleRepository.findByEventTypeAndIsActiveTrue(EventType.REDEMPTION))
                .thenReturn(Optional.of(rule));
        when(customerFranchiseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pointTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        RedemptionResponse res = redemptionService.confirmRedeem(1L, userId);

        assertEquals(1, customer.getCurrentPoints());
        verify(pointTransactionRepository).save(any());
    }

    @Test
    void findByRedemptionCode_found_success() {
        Redemption redemption = new Redemption();
        redemption.setRedemptionCode("RDM-ABC123");

        when(redemptionRepository.findByRedemptionCode("RDM-ABC123"))
                .thenReturn(Optional.of(redemption));

        Optional<Redemption> result = redemptionService.findByRedemptionCode("RDM-ABC123");

        assertTrue(result.isPresent());
        assertEquals("RDM-ABC123", result.get().getRedemptionCode());
    }

    @Test
    void findByRedemptionCode_notFound() {
        when(redemptionRepository.findByRedemptionCode("NOTFOUND"))
                .thenReturn(Optional.empty());

        Optional<Redemption> result = redemptionService.findByRedemptionCode("NOTFOUND");

        assertFalse(result.isPresent());
    }

    @Test
    void getAll_success() {
        Redemption r1 = new Redemption();
        r1.setId(1L);
        Redemption r2 = new Redemption();
        r2.setId(2L);

        when(redemptionRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RedemptionResponse> result = redemptionService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getByUserId_success() {
        UUID userId = UUID.randomUUID();
        Redemption r1 = new Redemption();
        r1.setId(1L);

        when(redemptionRepository
                .findByPointTransaction_CustomerFranchise_CustomerId(userId))
                .thenReturn(List.of(r1));

        List<RedemptionResponse> result = redemptionService.getByUserId(userId);

        assertEquals(1, result.size());
    }
}


