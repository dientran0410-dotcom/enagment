package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateCouponResponse {
    private Long promotionId;
    private Integer totalGenerated;
    private Integer successCount;
    private Integer failedCount;
    private List<String> generatedCodes;
    private Long executionTimeMs;
    private LocalDateTime generatedAt;
    private String message;
    private GenerationStats stats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationStats {
        private Integer totalRequested;
        private Integer totalGenerated;
        private Integer duplicatesSkipped;
        private Long executionTimeMs;
        private Double codesPerSecond;
    }
}

