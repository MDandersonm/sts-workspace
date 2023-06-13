package com.cos.securityex01.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.cos.securityex01.config.auth.PrincipalDetails;
import com.cos.securityex01.model.User;
import com.cos.securityex01.repository.UserRepository;

import lombok.*;
@Controller //View를 리턴하겠다라는것.
public class IndexController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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
	public @ResponseBody String user() {
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
	
}
