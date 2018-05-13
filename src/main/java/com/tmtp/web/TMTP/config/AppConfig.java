package com.tmtp.web.TMTP.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public Cloudinary cloudinary(@Value("${CLOUDINARY_API_KEY}") String apiKey,
                                 @Value("${CLOUDINARY_API_SECRET}") String apiSecret,
                                 @Value("${CLOUDINARY_API_CLOUD_NAME}") String cloudName) {

        Map config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
