package service.CSFC.CSFC_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.CouponCode;

import java.util.Optional;

@Repository
public interface CouponCodeRepository extends JpaRepository<CouponCode, Long> {
    Optional<CouponCode> findByCode(String code);
}
