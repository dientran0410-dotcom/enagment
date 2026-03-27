package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.*;
import service.CSFC.CSFC_auth_service.model.constants.VariantStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {
    private UUID id;
    private String sku;
    private String name;
    private BigDecimal price;
    private VariantStatus status;
}