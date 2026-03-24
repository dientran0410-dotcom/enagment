package service.CSFC.CSFC_auth_service.model.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RewardRequest {
    private UUID franchiseId;
    private String name;
    private Integer requiredPoints;
    private String description;
    private Boolean active;
    private MultipartFile imageFile;
}
