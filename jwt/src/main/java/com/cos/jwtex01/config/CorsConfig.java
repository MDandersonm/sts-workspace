package com.cos.jwtex01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);//내 서버가 응답을 할때 json을 javascript에서 처리할 수 있게 할지 설정하는것.
      config.addAllowedOrigin("*"); // e.g. http://domain1.com 어디에서든지 다 허용  // 모든 ip에 응답을 허용하겠다.
      config.addAllowedHeader("*");//모든 헤더에 응답을 허용
      config.addAllowedMethod("*");//모든 get,post등 요청 허용

      source.registerCorsConfiguration("/api/**", config);// /api/**로 들어오는 요청은 이 config 설정을 따르라는 말
      return new CorsFilter(source);
   }

}
