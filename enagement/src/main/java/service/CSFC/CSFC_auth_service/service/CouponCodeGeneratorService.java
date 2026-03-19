package service.CSFC.CSFC_auth_service.service;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import java.util.Optional;

import javax.swing.text.html.Option;
import java.util.Set;

public interface CouponCodeGeneratorService {

    /**
     * Generate unique coupon codes with high performance
     * @param quantity Number of codes to generate
     * @param length Length of random part (excluding prefix)
     * @param prefix Prefix for all codes (e.g., "SUMMER2026")
     * @param numericOnly Use only numbers (true) or alphanumeric (false)
     * @param existingCodes Set of existing codes to avoid duplicates
     * @return Set of unique generated codes
     */
    Set<String> generateUniqueCodes(
            int quantity,
            int length,
            String prefix,
            boolean numericOnly,
            Set<String> existingCodes
    );

    /**
     * Generate a single unique code
     */
    String generateSingleCode(String prefix, int length, boolean numericOnly);

    /**
     * Validate coupon code format
     */
    boolean validateCodeFormat(String code, String prefix, int expectedLength);

    /**
     * Calculate checksum for code validation
     */
    String calculateChecksum(String code);
    CouponCodeResponse getCouponCode(String code);
}

