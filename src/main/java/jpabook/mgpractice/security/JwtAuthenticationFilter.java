// JwtAuthenticationFilter.java
package jpabook.mgpractice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 클라이언트의 API 요청이 컨트롤러에 도착하기 전에 토큰을 검사하는 필터
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 클라이언트가 헤더에 담아 보낸 요청에서 JWT 토큰만 빼옴
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효한(위조/만료되지 않은) 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. Redis 에 이 토큰이 "logout" 처리되어 블랙리스트에 올라가 있는지 확인
            String isLogout = redisTemplate.opsForValue().get(token);

            // 4. 로그아웃된 토큰이 아니라면 정상적인 접근으로 인정
            if (ObjectUtils.isEmpty(isLogout)) {
                // 토큰에서 사용자 인증 정보를 꺼내옴
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // 스프링 시큐리티의 보안 공간에 이 사용자의 인증 정보를 저장하여 "로그인된 상태"로 유지
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 5. 다음 필터나 최종 목적지(컨트롤러)로 통과
        filterChain.doFilter(request, response);
    }

    // 요청 헤더("Authorization")에서 "Bearer "라는 문자열로 시작하는 토큰만 잘라내는 유틸리티 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 진짜 토큰 문자열만 반환
        }
        return null;
    }
}