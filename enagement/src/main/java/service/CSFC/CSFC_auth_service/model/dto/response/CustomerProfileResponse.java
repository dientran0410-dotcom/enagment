package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerProfileResponse {
    private String id; // UUID String từ API
    private String franchiseId;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String status;
    private boolean marketingOptin;
    private boolean isFirstLogin;
    private String role;

}
