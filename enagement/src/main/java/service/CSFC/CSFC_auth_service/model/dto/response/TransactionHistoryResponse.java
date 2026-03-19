package service.CSFC.CSFC_auth_service.model.dto.response;


import lombok.Builder;
import lombok.Data;
import service.CSFC.CSFC_auth_service.model.constants.ActionType;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistoryResponse {
    private Long id;
    private Integer amount;
    private ActionType actionType;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
}
