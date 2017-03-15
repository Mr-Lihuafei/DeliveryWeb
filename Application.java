package com.xxxair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.xxxair.medet.util.UserSecurityInterceptor;

@SpringBootApplication   
public class Application extends WebMvcConfigurerAdapter{
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class);
	}
	
	@Bean 
	public UserSecurityInterceptor initUserSecurityInterceptor(){
		return new UserSecurityInterceptor();
	}
	
	 public void addInterceptors(InterceptorRegistry registry) {  
	        registry.addInterceptor(initUserSecurityInterceptor()).addPathPatterns("/delivery/v1/mobile/auth/**"); 
	      
	 } 
	 
}
