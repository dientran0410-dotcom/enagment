package service.CSFC.CSFC_auth_service.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import service.CSFC.CSFC_auth_service.common.response.BaseResponse;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không chính xác");
//    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

//    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class, AuthorizationDeniedException.class})
//    public ResponseEntity<String> handleForbidden(ForbiddenException ex) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }



    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Object>> handleAppException(AppException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneralException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
    }


}
