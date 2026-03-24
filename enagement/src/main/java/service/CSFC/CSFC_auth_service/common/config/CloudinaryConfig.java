package service.CSFC.CSFC_auth_service.common.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", "dy91m8po5",
                "api_key", "853569831935591",
                "api_secret", "nDUg49fBMgtvpFxZu9F6p-wdGuc"
        ));
    }
}