package com.cos.securityex01.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.securityex01.config.oauth.PrincipalOauth2UserService;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
//prePostEnabled: @PreAuthorize @PostAuthorize 활성화
// securedEnabled: @Secured 활성화 .
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()//어떠한 ROLE이든 로그인되어있으면 접근가능
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()
		.and()
			.formLogin()//권한이 없는 경로로 접속했을때 로그인페이지로 튕겨준다.
			.loginPage("/loginForm")
			.loginProcessingUrl("/loginProc")//loginProc주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
//			<form action="/loginProc" method="post">이렇게 해주면 시큐리티가 알아서 로그인 해줌 컨트롤러를 만들필요없음
			.defaultSuccessUrl("/")//성공하면 메인페이지로
			.and()
			.oauth2Login()
			.loginPage("/loginForm")//구글로그인이 완료된 뒤의 후처리가 필요함.
			.userInfoEndpoint()
			.userService(principalOauth2UserService);//구글로그인이 완료가되면 코드를받는게아니라 액세스토큰+사용자프로필정보를 한방에 받음
		//1. 코드받기(인증완료-구글에로그인된 정상적사용자라는것)
		//2. 코드를 통해서 액세스토큰을받는다 이액세스토큰을 받으면 시큐리티서버가 구글로그인사용자정보에 접근할 권한이 생김
		//3.권한을 통해서 사용자 프로필정보를 가져옴
		//4-1 그 정보를 토대로 회원가입을 자동으로 진행
		//4-2 구글에는 이메일 전화번호 이름 아이디가 있는데 쇼핑몰에는 집주소,고객등급 이런게 필요함
		//이런 경우에는 추가적인 회원가입 창이 나와서 회원가입을 해야함
		//
	}
}





