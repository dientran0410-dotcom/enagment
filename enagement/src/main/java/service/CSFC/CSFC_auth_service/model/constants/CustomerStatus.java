package service.CSFC.CSFC_auth_service.model.constants;

public enum CustomerStatus {
    /**
     * Khách hàng đang hoạt động bình thường, có thể tích và tiêu điểm.
     */
    ACTIVE,

    /**
     * Khách hàng tạm thời không hoạt động (VD: Tự khóa, hoặc lâu không tương tác).
     */
    INACTIVE,

    /**
     * Tài khoản bị khóa do vi phạm chính sách hoặc gian lận điểm.
     * Không thể thực hiện bất kỳ giao dịch nào.
     */
    LOCKED,
    
    /**
     * Tài khoản chưa kích hoạt (nếu có quy trình đăng ký cần xác nhận email/OTP).
     */
    PENDING
}