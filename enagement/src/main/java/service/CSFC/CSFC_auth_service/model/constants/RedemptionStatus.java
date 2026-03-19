package service.CSFC.CSFC_auth_service.model.constants;

public enum RedemptionStatus {
    PENDING,    // Đang xử lý
    COMPLETED,  // Đã đổi thành công
    REJECTED,   // Bị từ chối
    CANCELLED,   // Khách huỷ
    EXPIRED     // Hết hạn
}