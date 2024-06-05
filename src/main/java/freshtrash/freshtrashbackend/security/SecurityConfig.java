package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.dto.security.OAuthAttributes;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.service.MemberService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/imgs/**", "/chat/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
            JwtTokenFilter jwtTokenFilter,
            Http401UnauthorizedAuthenticationEntryPoint authenticationEntryPoint,
            CustomOAuth2SuccessHandler customOAuth2SuccessHandler)
            throws Exception {
        return http.csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .regexMatchers("/oauth2.*", ".*auth/signup", ".*auth/signin", ".*mail.*")
                        .hasAnyRole("ANONYMOUS")
                        .regexMatchers(".*products(?:\\?.*)?", ".*auctions(?:\\?.*)?", "/chat-ws", ".*auth/check-nickname.*")
                        .permitAll()
                        .anyRequest()
                        .hasAnyRole("USER", "ADMIN"))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login(oAuth -> oAuth.userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler))
                .addFilterAfter(jwtTokenFilter, ExceptionTranslationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            MemberService memberService, PasswordEncoder passwordEncoder) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String userNameAttributeName = userRequest
                    .getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
            OAuthAttributes attributes =
                    OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
            String email = attributes.getEmail();
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

            // 회원이 존재하지 않는다면 저장하지 않고 회원가입이 안된 상태로 반환
            try {
                return MemberPrincipal.fromEntity(memberService.getMemberByEmail(email));
            } catch (MemberException e) {
                return MemberPrincipal.builder()
                        .email(email)
                        .password(dummyPassword)
                        .nickname(attributes.getName())
                        .authorities(UserRole.ANONYMOUS)
                        .build();
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService(MemberService memberService) {
        return email -> MemberPrincipal.fromEntity(memberService.getMemberByEmail(email));
    }

    @Bean
    public BCryptPasswordEncoder encoderPassword() {
        return new BCryptPasswordEncoder();
    }
}
