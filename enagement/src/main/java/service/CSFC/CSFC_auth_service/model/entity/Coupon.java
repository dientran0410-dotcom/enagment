package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENT, FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "min_order_value")
    private Double minOrderValue = 0.0;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit = 1; // Tổng số lần dùng toàn hệ thống

    @Column(name = "user_limit")
    private Integer userLimit = 1; // Số lần dùng per user

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_tier_id")
    private LoyaltyTier minTier; // Chỉ hạng này mới dùng được

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}