package service.CSFC.CSFC_auth_service.service;



import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.CreatePromotionRequest;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;

import java.util.List;

public interface PromotionService {
    // Chỉ khai báo hàm, không viết code xử lý
    Promotion createPromotion(CreatePromotionRequest request);

    List<Promotion> getActivePromotions(Long franchiseId);

    Promotion getPromotionById(Long id);

    Promotion updatePromotionStatus(Long id, PromotionStatus status);

    List<Promotion> getAllPromotions();
    
    // New methods
    Promotion updatePromotion(Long id, CreatePromotionRequest request);
    
    void deletePromotion(Long id);
    
    List<Promotion> getPromotionsByFranchise(Long franchiseId);
    
    List<Promotion> getPromotionsByStatus(PromotionStatus status);
    
    Object getPromotionCoupons(Long id);
    
    Object getPromotionStats(Long id);
    
    Object getDashboard();
}
