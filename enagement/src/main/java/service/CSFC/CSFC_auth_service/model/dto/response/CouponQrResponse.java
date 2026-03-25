package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;

@Data
@AllArgsConstructor
public class CouponQrResponse {

    private String code;
    private String redeemUrl;
    private Double discountValue;
    private DiscountType discountType;
}
