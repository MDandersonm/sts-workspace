package com.cos.jwtex01.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwtex01.config.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;


@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
//prePostEnabled: @PreAuthorize @PostAuthorize 활성화
// securedEnabled: @Secured 활성화 .
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final CorsFilter corsFilter;
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		http.addFilterBefore(new MyFilter3(),BasicAuthenticationFilter.class);// BasicAuthenticationFilter가 실행되기전에 실행된다라는말
		//filter1, filter2보다 securityConfig에 등록된 필터3가 우선적으로 먼저 실행된다. 
		//
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션만드는 방식 사용 안하기
		.and()
		.addFilter(corsFilter)//모든 요청은 이 필터를 탄다. 
		.formLogin().disable()//form로그인 안씀
		.httpBasic().disable()// 기본적인 http로그인방식 안씀( basic방식 : header에다가 authorization이라는 키값에 id,pw 담아서 인증하는 방식) -> 매번요청할떄마다 아이디랑비번 인증. 이러면 쿠키 필요없음. 암호화가안되서 중간에 노출될수 있따.
		//그래서 우리는 authorization에 토큰을 넣어보냄 (노출된다해도 id,pw가 아니라서 위험부담이적다.)  :  bearer방식
		.addFilter(new JwtAuthenticationFilter(authenticationManager()))//파라미터 : AuthenticationManager
		.authorizeRequests()
			.antMatchers("/api1/v1/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/api1/v1/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/api1/v1/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll();
//			.and()// formlogin을 diable해놔서 이 과정이 동작안함.
//			.formLogin()
//			.loginProcessingUrl("/login");
	}
}





