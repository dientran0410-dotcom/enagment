package service.CSFC.CSFC_auth_service.common.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    /**
     * 1. Cấu hình mức độ Log
     * NONE: Không log gì cả (Mặc định).
     * BASIC: Log method, URL và response status code.
     * HEADERS: Log thêm headers.
     * FULL: Log tất cả (Body, Headers, Metadata). Dùng cái này khi Dev để debug.
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 2. Cấu hình xử lý lỗi (Error Decoder)
     * Giúp chuyển đổi lỗi từ Service khác thành Exception nội bộ của mình.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * 3. (Tuỳ chọn) Request Interceptor
     * Nếu hệ thống có bảo mật, dùng cái này để tự động lấy Token từ request hiện tại
     * và gắn vào header "Authorization" khi gọi sang User/Order Service.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Ví dụ: Thêm header cố định hoặc lấy từ SecurityContextHolder
            requestTemplate.header("Content-Type", "application/json");
            // requestTemplate.header("Authorization", "Bearer " + token); 
        };
    }
}