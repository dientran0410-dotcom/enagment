package service.CSFC.CSFC_auth_service.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "csfc-auth-service", url = "${auth.service.url}")
public interface AuthServiceClient {


    @PostMapping("/api/auth-service/public/rbp/register")
    String registerServicePermissions(@RequestBody ServiceRbpRequest request);

}
