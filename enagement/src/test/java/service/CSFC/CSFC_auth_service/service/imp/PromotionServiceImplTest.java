package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.CreatePromotionRequest;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private UUID franchiseId;
    private CreatePromotionRequest request;

    @BeforeEach
    void setUp() {
        franchiseId = UUID.randomUUID();

        request = new CreatePromotionRequest();
        request.setFranchiseId(franchiseId);
        request.setName("Test Promotion");
        request.setDescription("Desc");
        request.setDiscountType(DiscountType.PERCENT);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(5));
    }

    // ===== createPromotion =====

    @Test
    void createPromotion_success() {
        Promotion saved = new Promotion();
        saved.setId(1L);

        when(promotionRepository.existsOverlappingPromotion(any(), any(), any(), any()))
                .thenReturn(false);
        when(promotionRepository.save(any())).thenReturn(saved);

        Promotion result = promotionService.createPromotion(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    void createPromotion_nullDates_shouldThrow() {
        request.setStartDate(null);

        assertThrows(IllegalArgumentException.class,
                () -> promotionService.createPromotion(request));

        verifyNoInteractions(promotionRepository);
    }

    @Test
    void createPromotion_startAfterEnd_shouldThrow() {
        request.setStartDate(LocalDateTime.now().plusDays(10));
        request.setEndDate(LocalDateTime.now().plusDays(5));

        assertThrows(IllegalArgumentException.class,
                () -> promotionService.createPromotion(request));
    }

    // ===== getPromotionById =====

    @Test
    void getPromotionById_found() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);

        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));

        Promotion result = promotionService.getPromotionById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getPromotionById_notFound_shouldThrow() {
        when(promotionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> promotionService.getPromotionById(1L));
    }

    // ===== updatePromotionStatus =====

    @Test
    void updatePromotionStatus_success() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);

        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any())).thenReturn(promotion);

        Promotion result = promotionService.updatePromotionStatus(1L, PromotionStatus.ACTIVE);

        assertEquals(PromotionStatus.ACTIVE, result.getStatus());
        verify(promotionRepository).save(promotion);
    }

    // ===== deletePromotion =====

    @Test
    void deletePromotion_success() {
        Promotion promotion = new Promotion();

        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));

        promotionService.deletePromotion(1L);

        verify(promotionRepository).delete(promotion);
    }

    // ===== updatePromotion =====

    @Test
    void updatePromotion_success() {
        Promotion promotion = new Promotion();

        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any())).thenReturn(promotion);

        Promotion result = promotionService.updatePromotion(1L, request);

        assertEquals(request.getName(), result.getName());
    }

    @Test
    void updatePromotion_invalidDates_shouldThrow() {
        request.setStartDate(LocalDateTime.now().plusDays(10));
        request.setEndDate(LocalDateTime.now().plusDays(5));

        Promotion promotion = new Promotion();
        when(promotionRepository.findById(1L))
                .thenReturn(Optional.of(promotion));

        assertThrows(IllegalArgumentException.class,
                () -> promotionService.updatePromotion(1L, request));
    }

    // ===== getDashboard =====

    @Test
    void getDashboard_success() {
        when(promotionRepository.count()).thenReturn(10L);
        when(promotionRepository.countByStatus(PromotionStatus.ACTIVE)).thenReturn(4L);
        when(promotionRepository.countByStatus(PromotionStatus.DRAFT)).thenReturn(3L);
        when(promotionRepository.findExpiringSoon(any(), any(), any()))
                .thenReturn(List.of(new Promotion(), new Promotion()));

        Object dashboard = promotionService.getDashboard();

        assertNotNull(dashboard);
    }

    // ========= STATUS & BOUNDARY DATES TESTS ==========

    @Test
    void updatePromotionStatus_activeToExpired_success() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setStatus(PromotionStatus.ACTIVE);

        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Promotion result = promotionService.updatePromotionStatus(1L, PromotionStatus.EXPIRED);

        assertEquals(PromotionStatus.EXPIRED, result.getStatus());
    }


    @Test
    void createPromotion_endDateBeforeStart_throwException() {
        UUID franchiseId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CreatePromotionRequest req = new CreatePromotionRequest();
        req.setFranchiseId(franchiseId);
        req.setName("Test Promo");
        req.setStartDate(now.plusDays(10));
        req.setEndDate(now.plusDays(5));

        assertThrows(IllegalArgumentException.class, () -> promotionService.createPromotion(req));
    }

    @Test
    void getPromotionsByStatus_active() {
        Promotion promo = new Promotion();
        promo.setStatus(PromotionStatus.ACTIVE);

        when(promotionRepository.findByStatus(PromotionStatus.ACTIVE))
                .thenReturn(List.of(promo));

        List<Promotion> result = promotionService.getPromotionsByStatus(PromotionStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals(PromotionStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void getPromotionsByStatus_draft() {
        Promotion promo = new Promotion();
        promo.setStatus(PromotionStatus.DRAFT);

        when(promotionRepository.findByStatus(PromotionStatus.DRAFT))
                .thenReturn(List.of(promo));

        List<Promotion> result = promotionService.getPromotionsByStatus(PromotionStatus.DRAFT);

        assertEquals(1, result.size());
        assertEquals(PromotionStatus.DRAFT, result.get(0).getStatus());
    }

    @Test
    void getPromotionById_notFound_throwException() {
        when(promotionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> promotionService.getPromotionById(999L));
    }

    @Test
    void getPromotionsByFranchise_success() {
        Promotion promo = new Promotion();
        promo.setId(1L);
        promo.setFranchiseId(franchiseId);

        when(promotionRepository.findByFranchiseId(franchiseId))
                .thenReturn(List.of(promo));

        List<Promotion> result = promotionService.getPromotionsByFranchise(franchiseId);

        assertEquals(1, result.size());
    }
}


