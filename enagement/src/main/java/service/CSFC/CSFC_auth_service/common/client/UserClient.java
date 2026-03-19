package service.CSFC.CSFC_auth_service.common.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.CSFC.CSFC_auth_service.common.client.dto.ExternalUserResponse;

// name: tên service trên Eureka, url: dùng khi test local hoặc không có Eureka
//@FeignClient(name = "user-service", url = "${services.product-service.url}")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ExternalUserResponse getUserById(@PathVariable("id") Long id);
}