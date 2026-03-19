package service.CSFC.CSFC_auth_service.common.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExternalOrderResponse {
    private UUID orderId; // Order ID thường là String (UUID hoặc mã đơn)
    private Long customerId;
    private BigDecimal totalAmount; // Cần cái này để tính điểm (VD: 10k = 1 điểm)
    private String status;
    private LocalDateTime orderDate;// COMPLETED mới được cộng điểm
}