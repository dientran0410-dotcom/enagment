package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.constants.TierName;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // Check if code exists
    boolean existsByCode(String code);

    // Find by code
    Optional<Coupon> findByCode(String code);

    // Batch check for existing codes
    @Query("SELECT c.code FROM Coupon c WHERE c.code IN :codes")
    List<String> findExistingCodes(@Param("codes") List<String> codes);

    // Find coupons by promotion
    List<Coupon> findByPromotionId(Long promotionId);

    // Count coupons by promotion
    Long countByPromotionId(Long promotionId);

    // Find codes starting with prefix
    @Query("SELECT c.code FROM Coupon c WHERE c.code LIKE :prefix%")
    List<String> findCodesStartingWith(@Param("prefix") String prefix);

    // Find active public coupons for customers
    @Query("""
    SELECT c FROM Coupon c
    JOIN c.promotion p
    JOIN c.minTier ct
    WHERE c.isPublic = true
      AND p.status = :status
      AND p.franchiseId = :franchiseId
      AND (p.startDate IS NULL OR p.startDate <= :now)
      AND (p.endDate IS NULL OR p.endDate >= :now)
      AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)
      AND (c.expiredAt IS NULL OR c.expiredAt >= :now)
      AND ct.name IN :allowedTiers
    ORDER BY c.createdAt DESC
""")
    List<Coupon> findActiveCouponsForCustomer(
            UUID franchiseId,
            LocalDateTime now,
            PromotionStatus status,
            List<TierName> allowedTiers
    );

    @Modifying
    @Query("UPDATE Coupon c SET c.usedCount = c.usedCount + 1 WHERE c.id = :id AND (c.usageLimit IS NULL OR c.usedCount < c.usageLimit)")
    int incrementUsageIfAvailable(@Param("id") Long id);
}