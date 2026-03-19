package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PointTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_franchise_id", nullable = false)
    private CustomerFranchise customerFranchise;

    @Column(nullable = false)
    private Integer amount; // Số điểm (+ hoặc -)

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private LoyaltyRule rule;

    @Column(name = "reference_id")
    private String referenceId; // Mã đơn hàng (Order ID)

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}