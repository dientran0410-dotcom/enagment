package service.CSFC.CSFC_auth_service.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.CSFC.CSFC_auth_service.mapper.CouponCodeMapper;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import service.CSFC.CSFC_auth_service.model.entity.CouponCode;
import service.CSFC.CSFC_auth_service.repository.CouponCodeRepository;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CouponCodeGeneratorServiceImpl implements CouponCodeGeneratorService {

    // Character sets for code generation
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Exclude I, O, 0, 1 to avoid confusion
    private static final String NUMERIC_CHARS = "0123456789";

    private final CouponCodeRepository couponCodeRepository;
    private final CouponCodeMapper couponCodeMapper;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public Set<String> generateUniqueCodes(
            int quantity,
            int length,
            String prefix,
            boolean numericOnly,
            Set<String> existingCodes
    ) {
        Set<String> generatedCodes = new HashSet<>(quantity);
        String charSet = numericOnly ? NUMERIC_CHARS : ALPHANUMERIC_CHARS;
        int charSetLength = charSet.length();

        // Calculate theoretical maximum unique codes
        long maxPossibleCodes = (long) Math.pow(charSetLength, length);

        if (quantity > maxPossibleCodes * 0.8) {
            throw new IllegalArgumentException(
                "Requested quantity (" + quantity + ") is too close to maximum possible unique codes (" + maxPossibleCodes + ")"
            );
        }

        int attempts = 0;
        int maxAttempts = quantity * 10; // Prevent infinite loop

        while (generatedCodes.size() < quantity && attempts < maxAttempts) {
            String code = generateCode(prefix, length, charSet, charSetLength);

            // Check if code is unique (not in existing codes and not in generated set)
            if (!existingCodes.contains(code) && generatedCodes.add(code)) {
                // Successfully added unique code
            }
            attempts++;
        }

        if (generatedCodes.size() < quantity) {
            throw new RuntimeException(
                "Could not generate " + quantity + " unique codes. Only generated " + generatedCodes.size() + " codes."
            );
        }

        return generatedCodes;
    }

    @Override
    public String generateSingleCode(String prefix, int length, boolean numericOnly) {
        String charSet = numericOnly ? NUMERIC_CHARS : ALPHANUMERIC_CHARS;
        return generateCode(prefix, length, charSet, charSet.length());
    }

    @Override
    public CouponCodeResponse getCouponCode(String code) {

        CouponCode couponCode = couponCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Not Found Coupon Code by Code: " + code));

        return couponCodeMapper.toResponse(couponCode);
    }

    /**
     * High-performance code generation using ThreadLocalRandom
     */
    private String generateCode(String prefix, int length, String charSet, int charSetLength) {
        StringBuilder code = new StringBuilder(prefix);

        // Use ThreadLocalRandom for better performance in multi-threaded scenarios
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSetLength);
            code.append(charSet.charAt(randomIndex));
        }

        return code.toString();
    }

    @Override
    public boolean validateCodeFormat(String code, String prefix, int expectedLength) {
        if (code == null || !code.startsWith(prefix)) {
            return false;
        }

        // Check total length (prefix + random part)
        return code.length() == prefix.length() + expectedLength;
    }

    @Override
    public String calculateChecksum(String code) {
        // Simple checksum using modulo 36 (for alphanumeric)
        int sum = 0;
        for (char c : code.toCharArray()) {
            sum += (int) c;
        }
        int checksum = sum % 36;

        // Convert to alphanumeric character
        if (checksum < 10) {
            return String.valueOf(checksum);
        } else {
            return String.valueOf((char) ('A' + checksum - 10));
        }
    }
}

