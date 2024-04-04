package freshtrash.freshtrashbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF 보호 비활성화
                .cors(cors -> cors // CORS 설정 활성화
                        .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                )
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/public/**").permitAll() // /public/** 경로는 인증 없이 접근 허용
                        .antMatchers("/admin/**").hasRole("ADMIN") // /admin/** 경로는 ADMIN 역할을 가진 사용자만 접근 가능
                        .antMatchers("/user/**").hasRole("USER") // /user/** 경로는 USER 역할을 가진 사용자만 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(withDefaults()); // 폼 기반 인증 활성화
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



