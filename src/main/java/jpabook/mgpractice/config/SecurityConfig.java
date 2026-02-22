// SecurityConfig.java
package jpabook.mgpractice.config;

import jpabook.mgpractice.security.JwtAuthenticationFilter;
import jpabook.mgpractice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 방어 기능을 끔 (REST API 서버는 세션 대신 토큰을 쓰기 때문에 꺼도 안전)
                .csrf(csrf -> csrf.disable())

                // 서버가 세션(Session)을 생성하지 않고 상태를 저장하지 않도록 설정 (JWT 환경의 필수 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 접근 권한 설정(어떤 URL 은 인증 없이 열어주고, 어떤 URL 은 잠글지 정함)
                .authorizeHttpRequests(auth -> auth
                        // 아래 경로들은 토큰(로그인) 없이 누구나 자유롭게 접근할 수 있도록 허용
                        .requestMatchers(
                                "/api/members/signup",
                                "/api/members/login",
                                "/api/members/reissue",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        // 그 외의 모든 요청은 반드시 인증(올바른 JWT 토큰)이 필요하다고 설정
                        .anyRequest().authenticated()
                )
                // 기본 로그인 화면(Form Login)과 기본 HTTP 인증 방식을 사용하지 않도록 끔
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 우리가 만든 검문소(JwtAuthenticationFilter)를 기본 시큐리티 필터 체인 앞단에 끼워넣음
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 로그인을 처리할 때 아이디/비밀번호 검증을 총괄하는 매니저를 스프링 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 비밀번호를 데이터베이스에 안전하게 암호화(해싱)해서 저장할 때 사용하는 도구
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}