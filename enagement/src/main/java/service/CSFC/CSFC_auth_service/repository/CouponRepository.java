package service.CSFC.CSFC_auth_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;

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

}