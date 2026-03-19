package service.CSFC.CSFC_auth_service.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import service.CSFC.CSFC_auth_service.common.client.dto.ExternalOrderResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", url = "${services.product-service.url}")
public interface OrderClient {

    @GetMapping("/api/order-service/internal/orders/customer")
    List<ExternalOrderResponse> getOrdersForCustomer(@RequestParam UUID customerId);
}