package service.CSFC.CSFC_auth_service.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import service.CSFC.CSFC_auth_service.model.dto.response.ProductVariantDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service", url = "${services.product-service.url}")
public interface ProductClient {
        @PostMapping("api/products/batch")
        List<ProductVariantDto> getVariantsByIds(@RequestBody List<UUID> ids);
}
