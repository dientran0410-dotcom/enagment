package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RewardResponse {
    private Long id;
    private UUID franchiseId;
    private String name;
    private Integer requiredPoints;
    private String description;
    private Boolean active;
    private String imageUrl;
}
