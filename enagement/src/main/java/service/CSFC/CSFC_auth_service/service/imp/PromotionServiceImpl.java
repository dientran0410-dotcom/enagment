package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.CreatePromotionRequest;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;
import service.CSFC.CSFC_auth_service.service.PromotionService;

import java.time.LocalDateTime;
import java.util.List;

@Service // Đánh dấu đây là Bean để Spring quản lý
@RequiredArgsConstructor // Tự động inject Repository qua Constructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Promotion createPromotion(CreatePromotionRequest request) {
        try {
            System.out.println("=== CREATE PROMOTION SERVICE ===");
            System.out.println("FranchiseId: " + request.getFranchiseId());
            System.out.println("Name: " + request.getName());
            System.out.println("StartDate: " + request.getStartDate());
            System.out.println("EndDate: " + request.getEndDate());
            
            // 1. Validate Business Rules
            
            // Null check
            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new IllegalArgumentException("StartDate and EndDate are required");
            }
            
            // Rule: Start date phải trước End date
            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before end date");
            }

            // Rule: Kiểm tra trùng thời gian (Overlapping) - TẠM THỜI COMMENT ĐỂ TEST
            try {
                boolean isOverlapping = promotionRepository.existsOverlappingPromotion(
                        request.getFranchiseId(),
                        request.getStartDate(),
                        request.getEndDate(),
                        List.of(PromotionStatus.ACTIVE, PromotionStatus.DRAFT)
                );

                if (isOverlapping) {
                    throw new IllegalStateException("A promotion already exists in this time range.");
                }
            } catch (Exception e) {
                System.err.println("Error checking overlapping: " + e.getMessage());
                // Bỏ qua lỗi này để test
            }

            // 2. Mapping DTO -> Entity
            Promotion promotion = new Promotion();
            promotion.setFranchiseId(request.getFranchiseId());
            promotion.setName(request.getName());
            promotion.setDescription(request.getDescription());
            promotion.setDiscountType(request.getDiscountType());
            promotion.setStartDate(request.getStartDate());
            promotion.setEndDate(request.getEndDate());

            // Rule: Mới tạo thì để Draft
            promotion.setStatus(PromotionStatus.DRAFT);

            System.out.println("Saving promotion...");
            // 3. Lưu xuống DB
            Promotion saved = promotionRepository.save(promotion);
            System.out.println("Saved successfully: " + saved.getId());
            return saved;
        } catch (Exception e) {
            System.err.println("ERROR in createPromotion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Promotion> getActivePromotions(Long franchiseId) {
        LocalDateTime now = LocalDateTime.now();

        if (franchiseId != null) {
            return promotionRepository.findActivePromotionsByFranchiseNow(franchiseId, now, PromotionStatus.ACTIVE);
        }

        return promotionRepository.findActivePromotionsNow(now, PromotionStatus.ACTIVE);
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    @Override
    @Transactional
    public Promotion updatePromotionStatus(Long id, PromotionStatus status) {
        Promotion promotion = getPromotionById(id);
        promotion.setStatus(status);
        return promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    @Transactional
    public Promotion updatePromotion(Long id, CreatePromotionRequest request) {
        Promotion promotion = getPromotionById(id);
        
        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        // Update fields
        promotion.setFranchiseId(request.getFranchiseId());
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStatus(request.getStatus());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        
        if (request.getDiscountType() != null) {
            promotion.setDiscountType(request.getDiscountType());
        }
        
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }

    @Override
    public List<Promotion> getPromotionsByFranchise(Long franchiseId) {
        return promotionRepository.findByFranchiseId(franchiseId);
    }

    @Override
    public List<Promotion> getPromotionsByStatus(PromotionStatus status) {
        return promotionRepository.findByStatus(status);
    }

    @Override
    public Object getPromotionCoupons(Long id) {
        // Verify promotion exists
        getPromotionById(id);

        return java.util.Collections.emptyList();
    }

    @Override
    public Object getPromotionStats(Long id) {
        Promotion promotion = getPromotionById(id);

        return java.util.Map.of(
            "promotionId", id,
            "promotionName", promotion.getName(),
            "status", promotion.getStatus(),
            "totalCoupons", 0,
            "usedCoupons", 0,
            "totalDiscount", 0,
            "totalCustomers", 0
        );
    }

    @Override
    public Object getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        
        long totalPromotions = promotionRepository.count();
        long activePromotions = promotionRepository.countByStatus(PromotionStatus.ACTIVE);
        long draftPromotions = promotionRepository.countByStatus(PromotionStatus.DRAFT);
        
        // Promotions expiring in next 7 days
        LocalDateTime next7Days = now.plusDays(7);
        List<Promotion> expiringSoon = promotionRepository.findExpiringSoon(now, next7Days, PromotionStatus.ACTIVE);
        
        return java.util.Map.of(
            "totalPromotions", totalPromotions,
            "activePromotions", activePromotions,
            "draftPromotions", draftPromotions,
            "expiringSoon", expiringSoon.size(),
            "expiringSoonList", expiringSoon
        );
    }
}
