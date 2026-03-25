package service.CSFC.CSFC_auth_service.service.imp;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CouponCodeGeneratorServiceImpl implements CouponCodeGeneratorService {

    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String NUMERIC_CHARS = "0123456789";

    @Override
    public String generateCouponCode(String prefix, int length, boolean numericOnly) {

        String charSet = numericOnly ? NUMERIC_CHARS : ALPHANUMERIC_CHARS;
        StringBuilder code = new StringBuilder(prefix);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            code.append(charSet.charAt(random.nextInt(charSet.length())));
        }

        return code.toString();
    }
}

