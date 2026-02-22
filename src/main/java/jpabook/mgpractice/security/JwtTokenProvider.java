// JwtTokenProvider.java
package jpabook.mgpractice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-milliseconds:3600000}") long accessTokenValidityTime,
            @Value("${jwt.refresh-token-validity-in-milliseconds:1209600000}") long refreshTokenValidityTime) {

        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
    }

    // 인증 정보를 바탕으로 액세스 토큰을 생성
    public String createAccessToken(Authentication authentication) {
        // 사용자의 권한 정보들을 쉼표(,)로 연결된 하나의 문자열로
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityTime); // 현재 시간 + 유효 시간 = 만료 시간이다.

        // JWT 토큰을 조립하여 문자열로 반환
        return Jwts.builder()
                .subject(authentication.getName()) // 토큰의 주인을 설정
                .claim("auth", authorities) // 토큰에 권한 정보
                .expiration(validity) // 토큰 만료 시간
                .signWith(key) // 서버만 아는 비밀키로 서명을 찍어 위조 방지
                .compact();
    }

    // 리프레시 토큰을 생성
    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityTime);

        return Jwts.builder()
                .subject(authentication.getName())
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 클라이언트가 보낸 토큰을 해석하여 스프링 시큐리티용 인증(Authentication) 객체를 다시 만듦
    public Authentication getAuthentication(String token) {
        // 비밀키를 이용해 토큰을 열어서 그 안의 정보(Payload/Claims)를 꺼냄
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        // 쉼표로 저장했던 권한 정보를 다시 분리하여 리스트로
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 스프링 시큐리티가 이해할 수 있는 UserDetails 객체로
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 최종적으로 컨트롤러 등에서 사용할 수 있는 인증 객체를 반환
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰이 위조되지 않았는지, 만료 기간이 지나지 않았는지 검사
    public boolean validateToken(String token) {
        try {
            // 토큰을 열어보는 과정에서 예외가 발생하지 않으면 정상 토큰
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 만료되었거나 형식이 올바르지 않으면 false 를 반환
            return false;
        }
    }

    // 로그아웃 블랙리스트 처리 등을 위해 토큰의 남은 만료 시간을 밀리초 단위로 계산
    public long getExpiration(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.getExpiration().getTime() - new Date().getTime();
    }
}