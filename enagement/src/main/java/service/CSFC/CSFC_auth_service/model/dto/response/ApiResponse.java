package service.CSFC.CSFC_auth_service.model.dto.response;

import java.util.Map;


public class ApiResponse <T> {

    private String status;
    private T payload;
    private Map<String, Object> error;
    private String message;

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.status = "SUCCESS";
        res.payload = data;
        res.message = message;
        return res;
    }


    public static ApiResponse<Void> error(Object details, String code) {
        ApiResponse<Void> res = new ApiResponse<>();
        res.status = "ERROR";
        res.error = Map.of(
                "code", code,
                "details", details
        );
        return res;
    }


    public String getStatus() {
        return status;
    }

    public T getPayload() {
        return payload;
    }

    public Map<String, Object> getError() {
        return error;
    }

    public String getMessage() {return message;}
}
