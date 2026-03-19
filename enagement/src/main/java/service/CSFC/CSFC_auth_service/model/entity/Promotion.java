package service.CSFC.CSFC_auth_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Promotion extends BaseEntity {

    @Column(name = "franchise_id")
    private Long franchiseId;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "discount_type")
    private String discountType;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Coupon> coupons;
}
