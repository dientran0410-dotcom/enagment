package service.CSFC.CSFC_auth_service.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import service.CSFC.CSFC_auth_service.model.dto.response.ApiResponse;
import service.CSFC.CSFC_auth_service.model.dto.response.CustomerProfileResponse;

import java.util.UUID;

@FeignClient(name = "csfc-auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {


    @PostMapping("/api/auth-service/public/rbp/register")
    String registerServicePermissions(@RequestBody ServiceRbpRequest request);

    @GetMapping("/api/auth-service/public/internal/customers/{customerId}/details")
    ApiResponse<CustomerProfileResponse> getCustomerProfile(@PathVariable("customerId") UUID customerId,
                                                           @RequestHeader("Authorization") String token);

}
