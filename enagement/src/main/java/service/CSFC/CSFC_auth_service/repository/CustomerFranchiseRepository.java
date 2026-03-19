package service.CSFC.CSFC_auth_service.repository;


import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.CustomerFranchise;

import java.util.Optional;

@Repository
public interface CustomerFranchiseRepository extends JpaRepository<CustomerFranchise, Long> {

    // Tìm khách hàng theo User ID và Franchise ID (để check xem họ đã từng mua hàng ở đây chưa)
    Optional<CustomerFranchise> findByCustomerIdAndFranchiseId(Long customerId, Long franchiseId);

    // Kiểm tra nhanh xem khách hàng tồn tại chưa (trả về true/false)
    boolean existsByCustomerIdAndFranchiseId(Long customerId, Long franchiseId);

    @Query("SELECT cf FROM CustomerFranchise cf " +
            "WHERE (:franchiseId IS NULL OR cf.franchiseId = :franchiseId) " +
            "AND (:tierId IS NULL OR cf.tier.id = :tierId)")
    Page<CustomerFranchise> findByFilters(
            @Param("franchiseId") Long franchiseId,
            @Param("tierId") Long tierId,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cf FROM CustomerFranchise cf WHERE cf.customerId =:id")
    Optional<CustomerFranchise> findByCustomerIdForUpdate(@Param("id") Long id);

}