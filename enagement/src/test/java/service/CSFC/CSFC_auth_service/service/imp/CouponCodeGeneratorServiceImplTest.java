package service.CSFC.CSFC_auth_service.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponCodeGeneratorServiceImplTest {

    private CouponCodeGeneratorServiceImpl service;

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String NUMERIC = "0123456789";

    @BeforeEach
    void setUp() {
        service = new CouponCodeGeneratorServiceImpl();
    }

    @Test
    void generateCouponCode_numericOnly_shouldContainOnlyDigits() {
        String prefix = "CPN-";
        int length = 8;

        String result = service.generateCouponCode(prefix, length, true);

        assertTrue(result.startsWith(prefix));
        assertEquals(prefix.length() + length, result.length());

        String generatedPart = result.substring(prefix.length());
        for (char c : generatedPart.toCharArray()) {
            assertTrue(NUMERIC.indexOf(c) >= 0,
                    "Character should be numeric: " + c);
        }
    }

    @Test
    void generateCouponCode_alphaNumeric_shouldContainValidChars() {
        String prefix = "SALE-";
        int length = 10;

        String result = service.generateCouponCode(prefix, length, false);

        assertTrue(result.startsWith(prefix));
        assertEquals(prefix.length() + length, result.length());

        String generatedPart = result.substring(prefix.length());
        for (char c : generatedPart.toCharArray()) {
            assertTrue(ALPHANUMERIC.indexOf(c) >= 0,
                    "Character should be alphanumeric: " + c);
        }
    }

    @Test
    void generateCouponCode_zeroLength_shouldReturnOnlyPrefix() {
        String prefix = "ZERO-";

        String result = service.generateCouponCode(prefix, 0, true);

        assertEquals(prefix, result);
    }

    @RepeatedTest(5)
    void generateCouponCode_shouldGenerateDifferentCodes() {
        String prefix = "RAND-";

        String code1 = service.generateCouponCode(prefix, 6, false);
        String code2 = service.generateCouponCode(prefix, 6, false);

        assertNotEquals(code1, code2);
    }
}
