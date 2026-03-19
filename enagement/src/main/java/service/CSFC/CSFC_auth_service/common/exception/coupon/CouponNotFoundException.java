package service.CSFC.CSFC_auth_service.common.exception.coupon;


import com.thoughtworks.xstream.core.BaseException;

public class CouponNotFoundException extends BaseException {
    public CouponNotFoundException() {
        super("COUPON_NOT_FOUND");
    }
}
