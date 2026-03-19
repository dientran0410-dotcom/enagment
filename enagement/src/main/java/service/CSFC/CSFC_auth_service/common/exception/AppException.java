package service.CSFC.CSFC_auth_service.common.exception;


public class AppException extends RuntimeException {
    private final int errorCode;

    public AppException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}