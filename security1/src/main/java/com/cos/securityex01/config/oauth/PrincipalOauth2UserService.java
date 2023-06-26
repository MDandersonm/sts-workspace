package com.cos.securityex01.config.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.securityex01.config.auth.PrincipalDetails;
import com.cos.securityex01.config.oauth.provider.FaceBookUserInfo;
import com.cos.securityex01.config.oauth.provider.GoogleUserInfo;
import com.cos.securityex01.config.oauth.provider.NaverUserInfo;
import com.cos.securityex01.config.oauth.provider.OAuth2UserInfo;
import com.cos.securityex01.model.User;
import com.cos.securityex01.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private UserRepository userRepository;
	
//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;
	/* 위와 같이 하면 순환참조 에러가난다.
	SecurityConfig는 PrincipalOauth2UserService에 의존하고 있습니다.
	왜냐하면 SecurityConfig 내에서 PrincipalOauth2UserService를 Autowired하고 있기 때문입니다.
	반면에, PrincipalOauth2UserService는 SecurityConfig에서 생성되는 BCryptPasswordEncoder 빈에 의존하고 있습니다.
	이로 인해 서로가 서로에게 의존하는 순환 의존성이 생성됩니다.
	*/
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(); // 직접 새 인스턴스 생성
		

	// userRequest 는 code를 받아서 accessToken을 응답 받은 객체
	//구글로 부터 받은 userRequest데이터에 대한 후처리 되는 함수
	//함수종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest); // google의 회원 프로필 조회

		// code를 통해 구성한 정보
		//registrationId로 어떤 oauth로 로그인했는지 알수있다.
		System.out.println("userRequest.clientRegistration : " + userRequest.getClientRegistration());
		//아래 토큰은 지금 별로 중요하지않다. 
		System.out.println("userRequest.getAccessToken: " + userRequest.getAccessToken());
		System.out.println("userRequest.getAccessToken.getTokenValue(): " + userRequest.getAccessToken().getTokenValue());
		System.out.println("userRequest.getClientRegistration().getRegistrationId() : " + userRequest.getClientRegistration().getRegistrationId());
		//구글로그인 버튼 클릭-> 구글로그인창->로그인완료->코드를 리턴받음(oauth library client가 받음)->코드를통해서  acess토큰을 요청
		//-> acess토큰을 받겟죠 여기까지가 userRequest정보 -> 회원프로필을받아야함 그떄사용되는 함수가 (loadUser함수) ->구글로부터 회원프로필 받아줌
		System.out.println("super.loadUser(userRequest).getAttributes() : " + super.loadUser(userRequest).getAttributes());//구글로부터 회원프로필을 받음
		// token을 통해 응답받은 회원정보
		System.out.println("oAuth2User : " + oAuth2User);
		
		System.out.println("oAuth2User.getAttributes() : " + oAuth2User.getAttributes());
		
		//회원가입을 강제로진행
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청~~");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
			
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청~~");
			oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
		}else {
			System.out.println("우리는 구글과 페이스북만 지원합니다.");
		}
		

		
		
//		String provider = userRequest.getClientRegistration().getRegistrationId();//google  .  구글만 사용할경우 이렇게 씀
		String provider = oAuth2UserInfo.getProvider();
		System.out.println("provider: "+provider);
//		String providerId= oAuth2User.getAttribute("sub");
		String providerId= oAuth2UserInfo.getProviderId();
		String username = provider+"_" +providerId;
		String password = bCryptPasswordEncoder.encode("겟인데어");
//		String email= oAuth2User.getAttribute("email");
		String email= oAuth2UserInfo.getEmail();
		String role= "Role_USER";
		//아이디 이미 회원가입되어있는지 확인절차
		User userEntity =userRepository.findByUsername(username);
		if(userEntity ==null) {//중복된 아이디가 없는경우
			userEntity=User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		}
			
		
		
//		return super.loadUser(userRequest);
		return new PrincipalDetails(userEntity,oAuth2User.getAttributes());//이게 만들어져서 Athuntication객체에 들어감.
//		return processOAuth2User(userRequest, oAuth2User);
		}
		
		
//
//	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
//
//		// Attribute를 파싱해서 공통 객체로 묶는다. 관리가 편함.
//		OAuth2UserInfo oAuth2UserInfo = null;
//		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
//			System.out.println("구글 로그인 요청~~");
//			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
//		} else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
//			System.out.println("페이스북 로그인 요청~~");
//			oAuth2UserInfo = new FaceBookUserInfo(oAuth2User.getAttributes());
//		} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
//			System.out.println("네이버 로그인 요청~~");
//			oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
//		} else {
//			System.out.println("우리는 구글과 페이스북만 지원해요 ㅎㅎ");
//		}
//
//		//System.out.println("oAuth2UserInfo.getProvider() : " + oAuth2UserInfo.getProvider());
//		//System.out.println("oAuth2UserInfo.getProviderId() : " + oAuth2UserInfo.getProviderId());
//		Optional<User> userOptional = 
//				userRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
//		
//		User user;
//		if (userOptional.isPresent()) {
//			user = userOptional.get();
//			// user가 존재하면 update 해주기
//			user.setEmail(oAuth2UserInfo.getEmail());
//			userRepository.save(user);
//		} else {
//			// user의 패스워드가 null이기 때문에 OAuth 유저는 일반적인 로그인을 할 수 없음.
//			user = User.builder()
//					.username(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
//					.email(oAuth2UserInfo.getEmail())
//					.role("ROLE_USER")
//					.provider(oAuth2UserInfo.getProvider())
//					.providerId(oAuth2UserInfo.getProviderId())
//					.build();
//			userRepository.save(user);
//		}
//
//		return new PrincipalDetails(user, oAuth2User.getAttributes());
//	}
}
