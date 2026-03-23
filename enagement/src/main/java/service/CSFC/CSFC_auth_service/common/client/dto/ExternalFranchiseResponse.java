package service.CSFC.CSFC_auth_service.common.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ExternalFranchiseResponse {
    private UUID id;
    private String name;
    private Boolean isActive; // Franchise còn hoạt động không
}