package com.tmtp.web.TMTP.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AnnotationSecurityConfiguration extends WebMvcConfigurerAdapter {

    private final AppReqInterceptor reqInterceptor;

    @Autowired
    public AnnotationSecurityConfiguration(AppReqInterceptor reqInterceptor) {
        this.reqInterceptor = reqInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(reqInterceptor);
    }
}
