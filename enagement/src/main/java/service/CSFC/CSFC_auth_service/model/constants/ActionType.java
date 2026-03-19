package service.CSFC.CSFC_auth_service.model.constants;

public enum ActionType {
    /**
     * Tích điểm: Cộng điểm từ đơn hàng hoặc hành động (Review, Share).
     * Dấu: Dương (+)
     */
    EARN,

    /**
     * Tiêu điểm: Trừ điểm khi đổi quà hoặc Voucher.
     * Dấu: Âm (-)
     */
    REDEEM,

    /**
     * Điều chỉnh tăng: Admin cộng bù điểm (do lỗi hệ thống hoặc CSKH tặng thêm).
     * Dấu: Dương (+)
     */
    ADJUST_ADD,

    /**
     * Điều chỉnh giảm: Admin trừ bớt điểm (do tính sai hoặc thu hồi).
     * Dấu: Âm (-)
     */
    ADJUST_SUB,

    /**
     * Hết hạn: Điểm bị trừ do quá hạn sử dụng.
     * Dấu: Âm (-)
     */
    EXPIRE,

    /**
     * Hoàn trả: Trừ lại điểm đã tích do khách trả hàng/hủy đơn.
     * Dấu: Âm (-)
     */
    REFUND
}