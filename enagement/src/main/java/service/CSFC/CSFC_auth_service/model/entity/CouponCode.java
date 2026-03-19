package service.CSFC.CSFC_auth_service.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.CSFC.CSFC_auth_service.model.constants.CodeStatus;

@Entity
@Table(name = "coupon_code")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    private CodeStatus status;

    @Column(name = "redeem_url")
    private String redeemUrl;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
