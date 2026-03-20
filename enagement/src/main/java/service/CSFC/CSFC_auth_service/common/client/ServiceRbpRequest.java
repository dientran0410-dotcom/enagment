package service.CSFC.CSFC_auth_service.common.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRbpRequest {
    private String serviceName;
    private List<RoleRbp> roles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleRbp {
        private String name;
        private List<String> permissions;
    }
}
