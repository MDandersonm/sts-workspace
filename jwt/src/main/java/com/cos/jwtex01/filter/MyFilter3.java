package com.cos.jwtex01.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		System.out.println("필터3");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		//토큰:코스
		//id,pw가 정상적으로 들어와서 로그인이 완료 되면 토큰을 만들어주고 그걸 응답해준다.
		//요청할때마다 header에 Authorization에 value값으로 토큰을 가지고 오겠죠
		//그떄 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면 됨(RSA,HS256)으로 토큰검증만 하면된다. 
		if(req.getMethod().equals("POST")) {
			String  headerAuth = req.getHeader("Authorization");
			System.out.println("req.getHeader(\"Authorization\") : "+headerAuth);
			if(headerAuth.equals("cos")) {
				chain.doFilter(req, res);//종료되지않고 진ㄴ행되도록
			}else {
				PrintWriter out = res.getWriter();
				out.println("인증안됨");
			}
		}
	}

}
