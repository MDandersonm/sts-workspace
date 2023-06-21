package com.cos.securityex01.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.securityex01.model.User;

import lombok.Data;
//시큐리티가 /loginProc 주소 요청이 오면 낚아채서 로그인을 진행시킨다
//로그인을 진행이 완료가되면 시큐리티session을 만들어줍니다.
//시큐리티가 자신만의 시큐리티세션을갖음
//Security ContextHolder라는 이 키값에다가 세션정보를 저장
//이 세션에 들어갈수있는 정보는, 시큐리티가 갖고있는 세션에 들어갈수있는 오브젝트가 정해져있다.
//Authentication타입의 객체여야 한다.
//Authentication안에 User정보가 있어야 됨.
//User오브젝트 타입 => UserDetails타입객체여야한다.
//즉 시큐리티세션 에다가 세션정보를 저장해주는데 들어갈수있는 객체가 Authentication 
//Authentication객체에 유저정보를 저장할때 UserDetails객체여야함.
//implements UserDetails 를 해서 쓰는 클래스 객체를 authentication객체에 넣을수있다.


// Authentication 객체에 저장할 수 있는 유일한 타입
@Data
public class PrincipalDetails implements UserDetails, OAuth2User{

	private User user;//콤포지션

	public PrincipalDetails(User user) {
		super();
		this.user = user;
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {//이계정이 만료됬니?
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {//이 계정이 잠겼니?
		return true;//아뇨
	}

	@Override
	public boolean isCredentialsNonExpired() {//비번을 너무 오래사용했니?
		return true;//아뇨
	}

	@Override
	public boolean isEnabled() {//이 계정이 활성화 되어있니?
		//1년동안 회원이 로그인을 안하면 휴면계정으로 하기로함
		//user.getLoginTime
		//현재시간-로그인시간 => 1년초과하면 return false;
		return true;
	}
	
	//해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
		collet.add(()->{ return user.getRole();});
		return collet;
	}
	
	/* 아래 식을 람다표현식으로 표현하면 위코드가됨
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collet = new ArrayList<GrantedAuthority>();
    collet.add(new GrantedAuthority() {
        @Override
        public String getAuthority() {
            return user.getRole();
        }
    });
    return collet;
}
	 */


	
}
