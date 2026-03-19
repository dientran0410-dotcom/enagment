package service.CSFC.CSFC_auth_service.common.exception.coupon;


import com.thoughtworks.xstream.core.BaseException;

public class InvalidCouponException extends BaseException {
    public InvalidCouponException(String message) {
        super("INVALID_COUPON");
    }
}
