package service.CSFC.CSFC_auth_service.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import service.CSFC.CSFC_auth_service.model.constants.DiscountType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateCouponRequest {

    @NotNull(message = "Promotion ID is required")
    private Long promotionId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Maximum 10,000 coupons per batch")
    private Integer quantity;

    @NotNull(message = "Code length is required")
    @Min(value = 6, message = "Code length must be at least 6")
    @Max(value = 20, message = "Code length must not exceed 20")
    private Integer codeLength;

    @NotBlank(message = "Code prefix is required")
    @Size(max = 10, message = "Prefix must not exceed 10 characters")
    private String codePrefix; // e.g., "SUMMER", "VIP", "NY2026"

    @NotNull(message = "Usage limit is required")
    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotBlank(message = "Status is required")
    private String status; // ACTIVE, INACTIVE

    // Coupon-specific fields
    @NotBlank(message = "Discount type is required")
    private DiscountType discountType; // FIXED_AMOUNT, PERCENTAGE

    @NotNull(message = "Discount value is required")
    @Min(value = 0, message = "Discount value must be positive")
    private Double discountValue;

    @Min(value = 0, message = "Min order value cannot be negative")
    private Double minOrderValue = 0.0;

    private Double maxDiscount;

    @Min(value = 1, message = "User limit must be at least 1")
    private Integer userLimit = 1;

    private Long minTierId;

    private Boolean isPublic = true;

    // Additional options
    private Boolean useNumericOnly; // true = only numbers, false = alphanumeric
    private Boolean includeChecksum; // true = add checksum for validation
}

