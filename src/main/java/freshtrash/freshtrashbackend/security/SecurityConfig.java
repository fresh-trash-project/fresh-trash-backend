package freshtrash.freshtrashbackend.security;

import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.service.MemberService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.SessionManagementFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        return http.csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .anyRequest()
                        .permitAll())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin(form -> form.usernameParameter("email"))
                .addFilterAfter(jwtTokenFilter, SessionManagementFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(MemberService memberService) {
        return email -> MemberPrincipal.fromEntity(memberService.getMemberEntityByEmail(email));
    }

    @Bean
    public BCryptPasswordEncoder encoderPassword() {
        return new BCryptPasswordEncoder();
    }
}
