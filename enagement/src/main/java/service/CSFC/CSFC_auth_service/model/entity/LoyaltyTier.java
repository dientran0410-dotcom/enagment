package service.CSFC.CSFC_auth_service.model.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import service.CSFC.CSFC_auth_service.infrastructure.BaseEntity;
import service.CSFC.CSFC_auth_service.model.constants.TierName;

@Entity
@Table(name = "loyalty_tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LoyaltyTier extends BaseEntity {

    @Column(name = "franchise_id", nullable = false)
    private Long franchiseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierName name;

    @Column(name = "min_point")
    private Integer minPoint;

    @Column(columnDefinition = "TEXT")
    private String benefits;

}