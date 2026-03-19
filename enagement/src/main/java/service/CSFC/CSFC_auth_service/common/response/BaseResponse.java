package service.CSFC.CSFC_auth_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private int statusCode;
    private boolean success;
    private String message;
    private T data;

    // Thành công
    public static <T> BaseResponse<T> success(String message, T data) {
        return BaseResponse.<T>builder()
                .statusCode(200)
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // Thất bại
    public static <T> BaseResponse<T> error(int statusCode, String message) {
        return BaseResponse.<T>builder()
                .statusCode(statusCode)
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}
