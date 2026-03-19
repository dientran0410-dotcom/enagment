package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Query kiểm tra xem có promotion nào đang ACTIVE mà trùng thời gian không
    // Logic overlapping: (StartA <= EndB) and (EndA >= StartB)
    @Query("SELECT COUNT(p) > 0 FROM Promotion p " +
           "WHERE p.franchiseId = :franchiseId " +
           "AND p.status IN :statuses  " +
           "AND (:startDate <= p.endDate AND :endDate >= p.startDate)")
    boolean existsOverlappingPromotion(@Param("franchiseId") Long franchiseId, 
                                       @Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("statuses") List<PromotionStatus> statuses);
    List<Promotion> findByStatus(PromotionStatus status);

    List<Promotion> findByStatusAndFranchiseId(PromotionStatus status, Long franchiseId);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.status IN :status " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now")
    List<Promotion> findActivePromotionsNow(@Param("now") LocalDateTime now,
                                            @Param("status") PromotionStatus status);

    @Query("SELECT p FROM Promotion p " +
            "WHERE p.status IN :status " +
            "AND p.franchiseId = :franchiseId " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now")
    List<Promotion> findActivePromotionsByFranchiseNow(
            @Param("franchiseId") Long franchiseId,
            @Param("now") LocalDateTime now,
            @Param("status") PromotionStatus status
    );

    // New methods
    List<Promotion> findByFranchiseId(Long franchiseId);
    
    long countByStatus(PromotionStatus status);
    
    @Query("SELECT p FROM Promotion p " +
            "WHERE p.endDate BETWEEN :now AND :futureDate " +
            "AND p.status IN :status " +
            "ORDER BY p.endDate ASC")
    List<Promotion> findExpiringSoon(@Param("now") LocalDateTime now, 
                                     @Param("futureDate") LocalDateTime futureDate,
                                     @Param("status") PromotionStatus status);
}
