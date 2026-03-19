package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RewardRequest {
    private Long franchiseId;
    private String name;
    private Integer requiredPoints;
    private String description;
    private Boolean active;
    private MultipartFile imageFile;
}
