package service.CSFC.CSFC_auth_service.model.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PointsBalanceResponse {
    private Long customerId;
    private Long franchiseId;
    private Integer currentPoints;
    private Integer totalEarnedPoints;
    private TierInfoDTO tier;
    private String status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TierInfoDTO {
        private Long tierId;
        private String tierName;
        private Integer minPoints;
        private String benefits;
    }
}
