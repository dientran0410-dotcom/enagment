package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserCoupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_franchise_id", nullable = false)
    private CustomerFranchise customerFranchise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "order_id")
    private String orderId; // Đơn hàng đã dùng coupon này

    private String status; // ASSIGNED, USED, EXPIRED

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;
}