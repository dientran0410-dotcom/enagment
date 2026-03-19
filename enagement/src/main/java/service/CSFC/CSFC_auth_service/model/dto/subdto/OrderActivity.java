package service.CSFC.CSFC_auth_service.model.dto.subdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderActivity {
    private UUID orderId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
}
