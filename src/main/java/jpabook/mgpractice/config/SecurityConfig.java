package jpabook.mgpractice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터 체인을 활성화합니다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 망분리 환경 및 개발 편의를 위해 CSRF 토큰 검증을 잠시 비활성화
                // 실무 은행망에서는 CSRF 방어가 필수
                .csrf(csrf -> csrf.disable())

                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 메인 페이지, 회원가입, 로그인 페이지 및 정적 리소스는 누구나 접근 가능
                        .requestMatchers(
                                "/signup",
                                "/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 그 외의 모든 요청(예: 계좌 이체, 조회 등)은 로그인한 사용자만 접근
                        .anyRequest().authenticated()
                )

                // 폼 로그인 설정 (기본 세션 기반 인증)
                .formLogin(form -> form
                        .loginPage("/login")             // 커스텀 로그인 페이지 URL
                        .loginProcessingUrl("/login-process") // HTML 폼에서 POST 요청을 보낼 주소 (시큐리티가 가로채서 처리함)
                        .usernameParameter("username")   // 폼의 아이디 input 태그 name
                        .passwordParameter("password")   // 폼의 비밀번호 input 태그 name
                        .defaultSuccessUrl("/", true)    // 로그인 성공 시 무조건 메인 페이지로 이동
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)     // 안전한 로그아웃을 위해 세션을 완전히 파기
                        .permitAll()
                )

                // H2 데이터베이스 콘솔 등 iframe을 사용하는 화면이 깨지지 않도록 옵션을 끔
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 비밀번호는 절대 평문으로 DB에 저장하면 안 됩니다.
    // BCrypt 해시 알고리즘을 사용하여 비밀번호를 단방향 암호화하는 빈(Bean)을 등록합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}