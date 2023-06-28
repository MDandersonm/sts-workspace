package com.cos.jwtex01.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;


@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
//prePostEnabled: @PreAuthorize @PostAuthorize 활성화
// securedEnabled: @Secured 활성화 .
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final CorsFilter corsFilter;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션만드는 방식 사용 안하기
		.and()
		.addFilter(corsFilter)//모든 요청은 이 필터를 탄다. 
		.formLogin().disable()//form로그인 안씀
		.httpBasic().disable()//기본적인 http로그인방식 안씀
		.authorizeRequests()
			.antMatchers("/api1/v1/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/api1/v1/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/api1/v1/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll();
	}
}





