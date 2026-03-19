package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.RedemptionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "redemption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Redemption extends BaseEntity {

    // Quan trọng: Link với CustomerFranchise (User tại cửa hàng đó)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_transaction_id", nullable = false)
    private PointTransaction pointTransaction;

    // Link với phần thưởng (nếu đổi quà hiện vật)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    // Link với khuyến mãi (nếu đổi voucher/promotion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed; // Số điểm đã trừ lúc đổi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RedemptionStatus status; // PENDING, COMPLETED...

    @Column(name = "redemption_code")
    private String redemptionCode; // Mã đổi quà (để đưa thu ngân scan)

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt; // Thời điểm đổi thực tế

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}