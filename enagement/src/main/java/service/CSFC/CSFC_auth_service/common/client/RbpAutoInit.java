package service.CSFC.CSFC_auth_service.common.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RbpAutoInit {

    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper;

    @Value("${rbp.config}")
    private String rbpConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            ServiceRbpRequest request = objectMapper.readValue(rbpConfig, ServiceRbpRequest.class);
            authServiceClient.registerServicePermissions(request);
            System.out.println("RBP registered for: " + request.getServiceName());
        } catch (Exception e) {
            System.out.println("RBP registration failed: " + e.getMessage());
        }
    }
}
