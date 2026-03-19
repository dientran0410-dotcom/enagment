package service.CSFC.CSFC_auth_service.common.exception;

public class QrGenerationException extends RuntimeException {

    public QrGenerationException(String message) {
        super("QR_GENERATION_ERROR: " + message);
    }
}
