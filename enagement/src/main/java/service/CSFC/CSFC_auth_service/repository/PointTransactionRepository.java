package service.CSFC.CSFC_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.CSFC.CSFC_auth_service.model.entity.PointTransaction;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    // Lấy lịch sử giao dịch của một khách hàng (Sắp xếp mới nhất lên đầu)
    List<PointTransaction> findAllByCustomerFranchiseIdOrderByCreatedAtDesc(Long customerFranchiseId);

    // Tìm giao dịch theo mã đơn hàng (để tránh cộng điểm 2 lần cho 1 đơn)
    boolean existsByReferenceId(String referenceId);
}