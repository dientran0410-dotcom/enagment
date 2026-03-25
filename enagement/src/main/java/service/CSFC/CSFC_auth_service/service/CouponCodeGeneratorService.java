package service.CSFC.CSFC_auth_service.service;

import java.util.Set;

public interface CouponCodeGeneratorService {

    String generateCouponCode(String prefix, int length, boolean numericOnly);
}

