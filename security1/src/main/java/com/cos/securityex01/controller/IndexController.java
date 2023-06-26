package com.cos.securityex01.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.securityex01.config.auth.PrincipalDetails;
import com.cos.securityex01.model.User;
import com.cos.securityex01.repository.UserRepository;

import lombok.*;
@Controller //View를 리턴하겠다라는것.
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) {	//의존성주입
		System.out.println("test/login===========");
		PrincipalDetails principalDetails =(PrincipalDetails)authentication.getPrincipal();
		///다운케스팅을 거쳐서 user오브젝트를 찾는방법
		System.out.println("principalDetails.getUser():"+ principalDetails.getUser());
		System.out.println("authentication.getPrincipal():"+ authentication.getPrincipal());
		
		//@authentication어노테이션을 통해서 user오브젝트를 찾는 방법
		System.out.println("userDetails.getUsername():"+userDetails.getUsername());
		System.out.println("userDetails.getUser():"+userDetails.getUser());
		
		//다운케스팅을 거쳐서 user오브젝트를 찾을수도있고
		//@authentication 어노테이션을통해서도 찾을수있다 두가지방법이있는거.
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication
			,@AuthenticationPrincipal OAuth2User oauth) {//DI의존성 주입
		//Authentication  또는  OAuth2User 로 user객체에 접근가능
		System.out.println("test/login===========");
		
		//OAuth2User를 통해 user객체 가져오기
		OAuth2User oauth2User =(OAuth2User)authentication.getPrincipal();
		System.out.println("oauth2User.getAttributes()"+ oauth2User.getAttributes());
		
		//@AuthenticationPrincipal 로 user객체가져오기
		System.out.println("oauth.getAttributes()"+oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	
	
	@GetMapping({ "", "/" })
	public @ResponseBody String index() {
		//머스테치 기본폴더: src/main/resources/
		//view resolver설정: template(prefix),  .mustache( suffix)  => application.yml에 설정되어있다. (기본경로라 생략가능하다)
		
		return "인덱스 페이지입니다.@@";
	}
//
//	@GetMapping("/user")
//	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principal) {
//		System.out.println("Principal : " + principal);
//		// iterator 순차 출력 해보기
//		Iterator<? extends GrantedAuthority> iter = principal.getAuthorities().iterator();
//		while (iter.hasNext()) {
//			GrantedAuthority auth = iter.next();
//			System.out.println(auth.getAuthority());
//		}
//
//		return "유저 페이지입니다.";
//	}
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails userDetails) {
		return "user 페이지입니다.";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "어드민 페이지입니다.";
	}
	
	//@PostAuthorize("hasRole('ROLE_MANAGER')")
	//@PreAuthorize("hasRole('ROLE_MANAGER')")
//	@Secured("ROLE_MANAGER")
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "매니저 페이지입니다.";
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	@GetMapping("/login")
	public @ResponseBody String login() {
		return "login!";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	@GetMapping("/join")
	public @ResponseBody String join() {
		return "join!!";
	}

	@PostMapping("/joinProc")
	public String joinProc(User user) {
		System.out.println("회원가입 진행 : " + user);
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "redirect:/loginForm";
	}
	
	
	//하나의 권한만 걸어줄거면 Secured로 쓰고 여러권한줄거면 PreAuthorize쓴다.
	@Secured("ROLE_ADMIN")//특정메서드에 role별 접근제한 걸기
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")//메서드가 실행되기 직전에 실행된다. 특정메서드에 role별 접근제한 걸기
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "data정보";
	}
	
/*
 * 스프링시큐리티는 자기만의 세션을들고있다(시큐리티 세션)
 * 즉 서버의 세션안에 시큐리티가관리하는 세션이 있다는말
 * 여기 시큐리시세션에 들어갈 수 있는 타입은 authentication객체만 가능
 * 필요할때마다 컨트롤러에서 DI가능 
 * authentication객체안에 들어갈수잇는 타입이 2가지가있는데 하나는 userDetails 하나는 OAuth2User타입
 * 언제 userDetails타입이 만들어지냐면 일반적인 로그인시.
 * OAuth로그인을 하게되면 oauth2user 타입이 객체안에 들어간다
 * 일반적으로 세션에 접근하려면 
 * @AuthenticationPrincipal PrincipalDetails userDetails
 * 로 받아야된다.
 * 
 */
}
