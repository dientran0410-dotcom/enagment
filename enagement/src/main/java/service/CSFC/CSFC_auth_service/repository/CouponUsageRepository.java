package service.CSFC.CSFC_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.constants.UsageStatus;
import service.CSFC.CSFC_auth_service.model.entity.CouponUsage;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    Optional<CouponUsage> findByCustomerIdAndCouponIdAndStatus(
            UUID customerId, Long couponId, UsageStatus status
    );

    long countByCustomerIdAndCouponIdAndStatus(
            UUID customerId, Long couponId, UsageStatus status
    );
}