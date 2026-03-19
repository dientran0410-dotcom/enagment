package service.CSFC.CSFC_auth_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.CustomerStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_franchise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerFranchise extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // ID lấy từ User Service

    @Column(name = "franchise_id", nullable = false)
    private Long franchiseId; // ID cửa hàng/nhượng quyền

    @Column(name = "current_points")
    private Integer currentPoints = 0;

    @Column(name = "total_earned_points")
    private Integer totalEarnedPoints = 0;

    // Quan hệ với LoyaltyTier
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id")
    private LoyaltyTier tier;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status; // ACTIVE, INACTIVE

    @Column(name = "first_order_at")
    private LocalDateTime firstOrderAt;

    @Column(name = "last_order_at")
    private LocalDateTime lastOrderAt;
    
    // Mapping ngược lại để dễ truy vấn (Optional)
    @OneToMany(mappedBy = "customerFranchise", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PointTransaction> transactions;
}