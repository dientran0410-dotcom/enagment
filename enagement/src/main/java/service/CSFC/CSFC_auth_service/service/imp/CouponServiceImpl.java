package service.CSFC.CSFC_auth_service.service.imp;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.CSFC.CSFC_auth_service.common.exception.ResourceNotFoundException;
import service.CSFC.CSFC_auth_service.common.exception.coupon.CouponNotFoundException;
import service.CSFC.CSFC_auth_service.common.exception.coupon.InvalidCouponException;
import service.CSFC.CSFC_auth_service.mapper.CouponMapper;
import service.CSFC.CSFC_auth_service.model.constants.PromotionStatus;
import service.CSFC.CSFC_auth_service.model.constants.UsageStatus;
import service.CSFC.CSFC_auth_service.model.dto.request.ApplyCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.CouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.request.GenerateCouponRequest;
import service.CSFC.CSFC_auth_service.model.dto.response.*;
import service.CSFC.CSFC_auth_service.model.entity.Coupon;
import service.CSFC.CSFC_auth_service.model.entity.CouponUsage;
import service.CSFC.CSFC_auth_service.model.entity.LoyaltyTier;
import service.CSFC.CSFC_auth_service.model.entity.Promotion;
import service.CSFC.CSFC_auth_service.repository.CouponRepository;
import service.CSFC.CSFC_auth_service.repository.CouponUsageRepository;
import service.CSFC.CSFC_auth_service.repository.LoyaltyTierRepository;
import service.CSFC.CSFC_auth_service.repository.PromotionRepository;
import service.CSFC.CSFC_auth_service.service.CouponCodeGeneratorService;
import service.CSFC.CSFC_auth_service.service.CouponService;
import service.CSFC.CSFC_auth_service.service.QrCodeService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    private final PromotionRepository promotionRepository;

    private final CouponCodeGeneratorService codeGeneratorService;

    private final LoyaltyTierRepository loyaltyTierRepository;

    private final CouponMapper couponMapper;

    private final CouponUsageRepository couponUsageRepository;

    String baseUrl = "https://api-gate-way.onrender.com";


    @Override
    @Transactional
    public GenerateCouponResponse generateCoupons(GenerateCouponRequest request) {

        long startTime = System.currentTimeMillis();

        // 1. Validate promotion
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new RuntimeException(
                        "Promotion not found with id: " + request.getPromotionId()));

        boolean numericOnly = request.getUseNumericOnly() != null && request.getUseNumericOnly();

        List<Coupon> coupons = new ArrayList<>();
        List<String> generatedCodes = new ArrayList<>();

        // 2. Generate từng coupon (mỗi coupon = 1 code)
        for (int i = 0; i < request.getQuantity(); i++) {

            String code;

            // 👉 đảm bảo unique code
            do {
                code = codeGeneratorService.generateCouponCode(
                        request.getCodePrefix(),
                        request.getCodeLength(),
                        numericOnly
                );
            } while (couponRepository.existsByCode(code));

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
            generatedCodes.add(code);
        }

        // 3. Save batch
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);

        long executionTime = System.currentTimeMillis() - startTime;

        // 4. Build response
        GenerateCouponResponse response = new GenerateCouponResponse();
        response.setPromotionId(request.getPromotionId());
        response.setTotalGenerated(savedCoupons.size());
        response.setSuccessCount(savedCoupons.size());
        response.setFailedCount(0);
        response.setGeneratedCodes(generatedCodes);
        response.setExecutionTimeMs(executionTime);
        response.setGeneratedAt(LocalDateTime.now());
        response.setMessage("Successfully generated " + savedCoupons.size() + " coupons");

        // 5. Stats
        GenerateCouponResponse.GenerationStats stats = new GenerateCouponResponse.GenerationStats();
        stats.setTotalRequested(request.getQuantity());
        stats.setTotalGenerated(savedCoupons.size());
        stats.setDuplicatesSkipped(0);
        stats.setExecutionTimeMs(executionTime);
        stats.setCodesPerSecond(
                executionTime > 0 ? (savedCoupons.size() * 1000.0) / executionTime : 0
        );

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
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {

        // 1. Map entity
        Coupon coupon = couponMapper.toEntity(request);

        // 2. Set promotion
        if (request.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(request.getPromotionId())
                    .orElseThrow(() -> new RuntimeException(
                            "Promotion not found with id: " + request.getPromotionId()));
            coupon.setPromotion(promotion);
        }

        // 3. Set loyalty tier (optional)
        if (request.getMinTierId() != null) {
            LoyaltyTier tier = loyaltyTierRepository.findById(request.getMinTierId())
                    .orElseThrow(() -> new RuntimeException(
                            "Tier not found with id: " + request.getMinTierId()));
            coupon.setMinTier(tier);
        }

        if (request.getCode() == null || request.getCode().isBlank()) {
            String generatedCode = codeGeneratorService.generateCouponCode("SALE-", 6, false);
            coupon.setCode(generatedCode);
        }

        // 4. Init usedCount
        coupon.setUsedCount(0);

        // 5. Validate cơ bản (optional nhưng nên có)
        if (coupon.getUsageLimit() == null || coupon.getUsageLimit() <= 0) {
            throw new RuntimeException("usageLimit phải > 0");
        }

        if (coupon.getUserLimit() == null || coupon.getUserLimit() <= 0) {
            coupon.setUserLimit(1); // default
        }

        // 6. Save
        Coupon savedCoupon = couponRepository.save(coupon);

        // 7. Return
        return couponMapper.toResponse(savedCoupon);
    }

    @Override
    @Transactional
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request) {

        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(CouponNotFoundException::new);

        UUID customerId = request.getCustomerId();

        // 1. Validate coupon
        validateCoupon(coupon, request);

        // 2. Check userLimit (đã USED)
        long usedCount = couponUsageRepository
                .countByCustomerIdAndCouponIdAndStatus(
                        customerId,
                        coupon.getId(),
                        UsageStatus.USED
                );

        if (usedCount >= coupon.getUserLimit()) {
            throw new InvalidCouponException("Bạn đã dùng hết lượt coupon này");
        }

        // 3. Check PENDING (REUSE)
        Optional<CouponUsage> pendingOpt =
                couponUsageRepository.findByCustomerIdAndCouponIdAndStatus(
                        customerId,
                        coupon.getId(),
                        UsageStatus.PENDING
                );

        CouponUsage usage;

        if (pendingOpt.isPresent()) {
            usage = pendingOpt.get();
        } else {
            usage = new CouponUsage();
            usage.setCustomerId(customerId);
            usage.setCoupon(coupon);
            usage.setStatus(UsageStatus.PENDING);
            usage.setCreatedAt(LocalDateTime.now());

            couponUsageRepository.save(usage);
        }

        // 4. Tính discount
        double discount = calculateDiscount(coupon, request.getOrderAmount());
        double finalAmount = request.getOrderAmount() - discount;

        return new ApplyCouponResponse(
                request.getOrderAmount(),
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
    public CouponQrResponse generateQrForCoupon(String code) {

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(CouponNotFoundException::new);

        String redeemUrl = baseUrl + "/api/coupons/apply?code=" + code;

        return new CouponQrResponse(
                coupon.getCode(),
                redeemUrl,
                coupon.getDiscountValue(),
                coupon.getDiscountType()
        );
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getActiveCouponsForCustomer() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findActiveCouponsForCustomer(now);
        return coupons.stream()
                .map(couponMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void checkoutCoupon(UUID customerId, String couponCode) {

        // 1. Tìm coupon
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(CouponNotFoundException::new);

        // 2. Tìm usage đang PENDING của user
        CouponUsage usage = couponUsageRepository
                .findByCustomerIdAndCouponIdAndStatus(
                        customerId,
                        coupon.getId(),
                        UsageStatus.PENDING
                )
                .orElseThrow(() ->
                        new InvalidCouponException("Không có coupon đang được áp dụng")
                );

        // 3. Double check usage limit (tránh race condition)
        long usedCountByUser = couponUsageRepository
                .countByCustomerIdAndCouponIdAndStatus(
                        customerId,
                        coupon.getId(),
                        UsageStatus.USED
                );

        if (usedCountByUser >= coupon.getUserLimit()) {
            throw new InvalidCouponException("Bạn đã dùng hết lượt coupon này");
        }

        // 4. Check usage global
        if (coupon.getUsageLimit() != null &&
                coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new InvalidCouponException("Coupon đã hết lượt sử dụng");
        }

        // 5. Update trạng thái
        usage.setStatus(UsageStatus.USED);

        // 6. Tăng số lần sử dụng global
        coupon.setUsedCount(coupon.getUsedCount() + 1);
    }

    @Override
    public List<CouponUsageResponse> getCustomerCouponUsage(UUID customerId) {

        List<CouponUsage> usages =
                couponUsageRepository.findByCustomerIdWithCoupon(customerId);

        return usages.stream()
                .map(couponMapper::mapToResponse)
                .toList();
    }

}
