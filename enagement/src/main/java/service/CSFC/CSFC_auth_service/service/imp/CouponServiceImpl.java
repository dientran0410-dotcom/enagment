package service.CSFC.CSFC_auth_service.service.imp;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.exception.coupon.CouponNotFoundException;
import service.CSFC.CSFC_auth_service.common.exception.coupon.InvalidCouponException;
import service.CSFC.CSFC_auth_service.mapper.CouponMapper;
import service.CSFC.CSFC_auth_service.model.constants.CodeStatus;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.ApplyCouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponCodeResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CouponResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.GenerateCouponResponse;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.CouponCode;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.repository.CouponCodeRepository;
import service.CSFC.CSFC_auth_service.repository.CouponRepository;
import service.CSFC.CSFC_auth_service.repository.LoyaltyTierRepository;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;
import service.CSFC.CSFC_auth_service.service.CouponService;
import service.CSFC.CSFC_auth_service.service.QrCodeService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    private final PromotionRepository promotionRepository;

    private final CouponCodeGeneratorService codeGeneratorService;

    private final LoyaltyTierRepository loyaltyTierRepository;

    private final CouponMapper couponMapper;

    private final QrCodeService qrCodeService;

    private final CouponCodeRepository couponCodeRepository;

    @Override
    @Transactional
    public GenerateCouponResponse generateCoupons(GenerateCouponRequest request) {
        long startTime = System.currentTimeMillis();

        // Validate promotion exists
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + request.getPromotionId()));

        // Get existing codes with same prefix to avoid duplicates
        List<String> existingCodesList = couponRepository.findCodesStartingWith(request.getCodePrefix());
        Set<String> existingCodes = new HashSet<>(existingCodesList);

        // Generate unique codes
        boolean numericOnly = request.getUseNumericOnly() != null && request.getUseNumericOnly();
        Set<String> generatedCodes = codeGeneratorService.generateUniqueCodes(
                request.getQuantity(),
                request.getCodeLength(),
                request.getCodePrefix(),
                numericOnly,
                existingCodes);

        // Create coupon entities
        List<Coupon> coupons = new ArrayList<>();
        for (String code : generatedCodes) {
            Coupon coupon = new Coupon();
            coupon.setCode(code);
            coupon.setPromotion(promotion);
            coupon.setUsageLimit(request.getUsageLimit());
            coupon.setUserLimit(request.getUserLimit() != null ? request.getUserLimit() : 1);
            coupon.setUsedCount(0);
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : 0.0);
            coupon.setMaxDiscount(request.getMaxDiscount());
            coupon.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);

            if (request.getMinTierId() != null) {
                LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                        .orElseThrow(() -> new RuntimeException(
                                "Loyalty tier not found with id: " + request.getMinTierId()));
                coupon.setMinTier(tier);
            }

            coupons.add(coupon);
        }

        // Bulk insert using saveAll (batch processing)
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Build response
        GenerateCouponResponse response = new GenerateCouponResponse();
        response.setPromotionId(request.getPromotionId());
        response.setTotalGenerated(savedCoupons.size());
        response.setSuccessCount(savedCoupons.size());
        response.setFailedCount(0);
        response.setGeneratedCodes(new ArrayList<>(generatedCodes));
        response.setExecutionTimeMs(executionTime);
        response.setGeneratedAt(LocalDateTime.now());
        response.setMessage("Successfully generated " + savedCoupons.size() + " coupon codes");

        // Add statistics
        GenerateCouponResponse.GenerationStats stats = new GenerateCouponResponse.GenerationStats();
        stats.setTotalRequested(request.getQuantity());
        stats.setTotalGenerated(savedCoupons.size());
        stats.setDuplicatesSkipped(request.getQuantity() - savedCoupons.size());
        stats.setExecutionTimeMs(executionTime);
        stats.setCodesPerSecond(executionTime > 0 ? (savedCoupons.size() * 1000.0) / executionTime : 0);
        response.setStats(stats);

        return response;
    }

    @Override
    public boolean validateCoupon(String code) {
        return couponRepository.existsByCode(code);
    }

    @Override
    public GenerateCouponResponse.GenerationStats getGenerationStats(Long promotionId) {
        // Đếm tổng số coupon trong database cho promotion này
        Long totalCoupons = couponRepository.countByPromotionId(promotionId);

        GenerateCouponResponse.GenerationStats stats = new GenerateCouponResponse.GenerationStats();
        // totalGenerated = tổng coupon hiện có trong DB
        stats.setTotalGenerated(totalCoupons.intValue());

        // Các field này chỉ có giá trị trong response của generateCoupons()
        // Endpoint /stats chỉ trả về snapshot hiện tại của database
        stats.setTotalRequested(totalCoupons.intValue()); // Số hiện có
        stats.setDuplicatesSkipped(0);
        stats.setExecutionTimeMs(null);
        stats.setCodesPerSecond(null);

        return stats;
    }

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Override
    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    @Override
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        couponMapper.updateEntity(existing, request);

        if (request.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));
            existing.setPromotion(promotion);
        }

        if (request.getMinTierId() != null) {
            LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                    .orElseThrow(() -> new RuntimeException("Tier not found"));
            existing.setMinTier(tier);
        }

        Coupon updated = couponRepository.save(existing);

        return couponMapper.toResponse(updated);
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {

        Coupon coupon = new Coupon();
        coupon = couponMapper.toEntity(request);

        if (request.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + request.getPromotionId()));
            coupon.setPromotion(promotion);
        }

        if (request.getMinTierId() != null) {
            LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                    .orElseThrow(() -> new RuntimeException("Tier not found with id: " + request.getMinTierId()));
            coupon.setMinTier(tier);
        }

        coupon = couponRepository.save(coupon);

        List<CouponCode> codes = new ArrayList<>();

        for(int i = 0; i < request.getUsageLimit(); i++) {

            String code = generateCode();

            CouponCode couponCode = new CouponCode();
            couponCode.setCode(code);
            couponCode.setStatus(CodeStatus.AVAILABLE);
            couponCode.setCoupon(coupon);

            codes.add(couponCode);
        }
        couponCodeRepository.saveAll(codes);

        return couponMapper.toResponse(coupon);
    }

    @Override
    @Transactional
    public ApplyCouponResponse applyCoupon(String code, double orderAmount) {
        CouponCode couponCode = couponCodeRepository
                .findByCode(code)
                .orElseThrow(CouponNotFoundException::new);

        if (couponCode.getStatus() != CodeStatus.PENDING) {
            throw new InvalidCouponException("QR không hợp lệ");
        }

        Coupon coupon = couponCode.getCoupon();

        // 🔥 vẫn check hạn bằng Promotion
        validateCoupon(coupon, new ApplyCouponRequest(code, orderAmount));

        double discount = calculateDiscount(coupon, orderAmount);
        double finalAmount = orderAmount - discount;

        couponCode.setStatus(CodeStatus.USED);
        couponCodeRepository.save(couponCode);

        return new ApplyCouponResponse(
                orderAmount,
                discount,
                finalAmount
        );
    }

    private double calculateDiscount(Coupon coupon, double amount) {

        double discount = 0;

        if ("FIXED_AMOUNT".equals(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        }

        if ("PERCENTAGE".equals(coupon.getDiscountType())) {

            discount = amount * coupon.getDiscountValue() / 100;

            if (coupon.getMaxDiscount() != null && coupon.getMaxDiscount() > 0) {
                discount = Math.min(discount, coupon.getMaxDiscount());
            }
        }

        return Math.min(discount, amount);
    }

    private void validateCoupon(Coupon coupon,
            ApplyCouponRequest req) {
        LocalDateTime now = LocalDateTime.now();
        Promotion promotion = coupon.getPromotion();

        Integer usageLimit = coupon.getUsageLimit();
        int usedCount = coupon.getUsedCount() == null ? 0 : coupon.getUsedCount();
        if (usageLimit != null && (usageLimit == 0 || usedCount >= usageLimit)) {
            throw new InvalidCouponException("Mã giảm giá hết lượt dùng");
        }

        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            throw new InvalidCouponException("Mã giảm giá đã hết hạn");
        }

        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            throw new InvalidCouponException("Mã giảm giá chưa bắt đầu");
        }

        double minOrderValue = coupon.getMinOrderValue() == null ? 0.0 : coupon.getMinOrderValue();
        if (req.getOrderAmount() < minOrderValue) {
            throw new InvalidCouponException("Tổng số tiền đơn hàng không đủ để áp dụng mã giảm giá");
        }

        if (promotion.getStatus() != PromotionStatus.ACTIVE) {
            throw new InvalidCouponException("Hiện không có chương trình khuyến mãi này!");
        }

        if (!Boolean.TRUE.equals(coupon.getIsPublic())) {
            throw new InvalidCouponException("Bạn không có quyền sử dụng mã giảm giá này!");
        }
    }

    @Override
    @Transactional
    public CouponCodeResponse generateQrForCoupon(String code) {

        CouponCode couponCode = couponCodeRepository
                .findByCode(code)
                .orElseThrow(CouponNotFoundException::new);

        if (couponCode.getStatus() != CodeStatus.AVAILABLE) {
            throw new InvalidCouponException("Mã không khả dụng");
        }

        Coupon coupon = couponCode.getCoupon();

        if (coupon.getPromotion().getStatus() != PromotionStatus.ACTIVE) {
            throw new InvalidCouponException("Coupon không hoạt động");
        }


        String redeemUrl =
                "https://localhost:8081/api/coupons/redeem?code=" + code;


        couponCode.setStatus(CodeStatus.PENDING);
        couponCodeRepository.save(couponCode);


        CouponCodeResponse response = new CouponCodeResponse();
        response.setCode(code);
        response.setRedeemUrl(redeemUrl);
        response.setDiscountValue(coupon.getDiscountValue());

        return response;
    }

    private CouponResponse mapToResponse(Coupon coupon) {

        CouponResponse response = new CouponResponse();

        response.setId(coupon.getId());
        response.setPromotionId(coupon.getPromotion().getId());
        response.setCode(coupon.getCode());
        response.setDiscountType(coupon.getDiscountType());
        response.setDiscountValue(coupon.getDiscountValue());
        response.setMinOrderValue(coupon.getMinOrderValue());
        response.setMaxDiscount(coupon.getMaxDiscount());
        response.setUsageLimit(coupon.getUsageLimit());
        response.setUserLimit(coupon.getUserLimit());
        response.setUsedCount(coupon.getUsedCount());
        response.setMinTier(coupon.getMinTier());
        response.setIsPublic(coupon.getIsPublic());

        return response;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

}
