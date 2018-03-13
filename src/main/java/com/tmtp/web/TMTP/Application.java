package com.tmtp.web.TMTP;

import com.tmtp.web.TMTP.service.StorageProperties;
import com.tmtp.web.TMTP.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableScheduling
public class Application extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
		registry.addResourceHandler("/img/kits/footballs/**").addResourceLocations("classpath:/static/img/kits/footballs/");
		registry.addResourceHandler("/img/kits/jackets/**").addResourceLocations("classpath:/static/img/kits/jackets/");
		registry.addResourceHandler("/img/kits/shorts/**").addResourceLocations("classpath:/static/img/kits/shorts/");
		registry.addResourceHandler("/img/kits/socks/**").addResourceLocations("classpath:/static/img/kits/socks/");
		registry.addResourceHandler("/img/overlays/**").addResourceLocations("classpath:/static/img/overlays/");
		registry.addResourceHandler("/img/profile/**").addResourceLocations("classpath:/static/img/profile");
		registry.addResourceHandler("/img/ads/desktop/**").addResourceLocations("classpath:/static/img/ads/desktop");
		registry.addResourceHandler("/img/ads/mobile/**").addResourceLocations("classpath:/static/img/ads/mobile");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/static/vendor/");
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			//storageService.deleteAll();
			storageService.init();
		};
	}
}
