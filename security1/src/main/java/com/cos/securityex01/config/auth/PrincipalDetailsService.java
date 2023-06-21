package com.cos.securityex01.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.securityex01.model.User;
import com.cos.securityex01.repository.UserRepository;

//시큐리티 설정에서 loginProcessingUrl("/loginProc");
//loginProc 요청이 오면 자동으로 userDetailsService타입으로 IOC되어있는 loadUserByUsername 함수가 실행된다.

@Service
public class PrincipalDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	//String username 자리는  input에서 name속성으로 지정한 명칭을 그대로 작성해줘야한다.
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		System.out.println("user.getRole():"+user.getRole());
		if(user == null) {
			return null;
		}
		return new PrincipalDetails(user);
		//시큐리티 세션 <-- Authentication <--- UserDetails  
		//리턴된  new PrincipalDetails(user)값이 Authentication내부로 들어감.
		// 그리고 바로 시큐리티 세션내부에 Authentication객체가 들어감
	}

}
