package com.cos.jwtex01.config.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtex01.config.auth.PrincipalDetails;
import com.cos.jwtex01.dto.LoginRequestDto;
import com.cos.jwtex01.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

//스프링시큐리티에서  UsernamePasswordAuthenticationFilter가 필터가 있음.
// /login 요청해서 username, password 전송하면(post) UsernamePasswordAuthenticationFilter가 동작함
// 이게 동작하지않는 이유는 security config에서 formlogin을 disable했기 때문이다
//  작동시킬려면 JwtAuthenticationFilter 를 다시 security config에 등록해야함

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	
	// Authentication 객체 만들어서 리턴 => 의존 : AuthenticationManager
	// 인증 요청시에 실행되는 함수 => /login
	// /login 요청을 하면 UsernamePasswordAuthenticationFilter가 낚아채서 로그인 시도를 위해서 실행되는 함수이다. 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		System.out.println("JwtAuthenticationFilter의 attemptAuthentication메서드 : 진입");
		//1.username과 password를 받아서 
		try {
//			BufferedReader br= request.getReader();
//			String input = null;
//			while((input=br.readLine()) !=null) {
//				System.out.println("input:"+input);
//			}
			System.out.println(request.getInputStream().toString());//Stream안의 바이트안에 id와 pw가 담겨있음
			
			
			ObjectMapper om = new ObjectMapper();//제이슨데이터를 파싱을 해준다. 
			User user = om.readValue(request.getInputStream(), User.class);
			System.out.println("user:"+user);

			//로그인시도하려면 토큰을 만들어야한다.
			UsernamePasswordAuthenticationToken authenticationToken 
					= new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			//이 토큰으로 로그인시도를 한다. 
			//authenticationManager에 토큰을 넣어서 던지면 인증일 해줘서 authentication을 받겠죠
			// authentication에 내 로그인한 정보가 담긴다. 
			//authentication가 만들어졌다는건 로그인이 제대로 됐다는 얘기이다.
			//DB에 있는 username과 password가 일치한다는 얘기
			Authentication authentication = 
					authenticationManager.authenticate(authenticationToken);
			
			//PrincipaldetailsSeervice의 loadUserByUsername()함수가 실행됨.
			//  loadUserByUsername()얘는 username만 받음 pw는 스프링이 데이터베이스에서 알아서 처리해줌. 
			PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
			//=>아래가 출력이 잘되면  로그인이 되었다는뜻
			System.out.println("로그인 완료됨 Authentication : "+principalDetailis.getUser().getUsername());
			//authentication객체가 session영역에 저장됨 . 리턴의 이유는 권한관리를 security가 대신 해주기 때문에 편하려고 하는 것
			//굳이 JWT토큰을 사용하면서 세션을 만들 이유가 없음. 단지 권한처리때문에 session에 넣어줌
			return authentication;//리턴된 authentication이 세션에 저장됨
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
		//2. 정상인지 로그인 시도를 해보는거에요
		//이때 가장 간단한 방법이 authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출
		//그러면 loadUserByUsername()이 실행이된다. 정상적으로 principalDetails가 리턴이되면 
		//3. principalDetails를 세션에 담고(세션에 안담으면 권한관리가안됨 securityConfig에서 antMatcher로 권한관리..)
		//4. 마지막으로 JWT토큰을 만들어서 응답해주면 됨
		
//		====================모범답안=======================================================================
//		// request에 있는 username과 password를 파싱해서 자바 Object로 받기
//		ObjectMapper om = new ObjectMapper();//제이슨데이터를 파싱을 해준다. 
//		LoginRequestDto loginRequestDto = null;
//		try {
//			loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println("JwtAuthenticationFilter : "+loginRequestDto);
//		
//		// 유저네임패스워드 토큰 생성
//		UsernamePasswordAuthenticationToken authenticationToken = 
//				new UsernamePasswordAuthenticationToken(
//						loginRequestDto.getUsername(), 
//						loginRequestDto.getPassword());
//		
//		System.out.println("JwtAuthenticationFilter : 토큰생성완료");
//		
//		// authenticate() 함수가 호출 되면 인증 프로바이더가 유저 디테일 서비스의
//		// loadUserByUsername(토큰의 첫번째 파라메터) 를 호출하고
//		// UserDetails를 리턴받아서 토큰의 두번째 파라메터(credential)과
//		// UserDetails(DB값)의 getPassword()함수로 비교해서 동일하면
//		// Authentication 객체를 만들어서 필터체인으로 리턴해준다.
//		
//		// Tip: 인증 프로바이더의 디폴트 서비스는 UserDetailsService 타입
//		// Tip: 인증 프로바이더의 디폴트 암호화 방식은 BCryptPasswordEncoder
//		// 결론은 인증 프로바이더에게 알려줄 필요가 없음.
//		Authentication authentication = 
//				authenticationManager.authenticate(authenticationToken);
//		
//		PrincipalDetails principalDetailis = (PrincipalDetails) authentication.getPrincipal();
//		System.out.println("Authentication : "+principalDetailis.getUser().getUsername());
//		return authentication;
	}

	//attempAuthentication실행후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다
	//JWT토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response해주면 됨
	// JWT Token 생성해서 response에 담아주기
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행됨: 인증이완료되었다는 뜻임");
		
		
		
		PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();
		
		//RSA방식은 아니고 Hash암호방식
		String jwtToken = JWT.create()
				.withSubject(principalDetailis.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))//토큰의 유효시간(현재시간+만료시간)
				.withClaim("id", principalDetailis.getUser().getId())//넣고싶은 키-밸류 값 이런식으로 넣어주면 됨
				.withClaim("username", principalDetailis.getUser().getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));//시크릿값
		
		//사용자에게 응답할 response 헤더에 담아줌
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);
		//이제 토큰을 통해서 처리하는 필터가 하나 필요하다 토큰을 통해서 중요한정보에 접근할수있게 서버는 jwt토큰이 유효한지를 판단하는 필터를 만들어야함
		//JwtAuthorizationFilter를만든다.
	}
	
}
