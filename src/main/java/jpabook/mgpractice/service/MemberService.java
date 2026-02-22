// MemberService.java
package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Member;
import jpabook.mgpractice.dto.SignupRequest;
import jpabook.mgpractice.dto.TokenResponse;
import jpabook.mgpractice.dto.UpdateMemberRequest;
import jpabook.mgpractice.mapper.MemberMapper;
import jpabook.mgpractice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper; // MyBatis 로 DB와 통신하는 매퍼
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 도구
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성 및 검증 도구
    private final AuthenticationManager authenticationManager; // 아이디/비밀번호 검증 매니저
    private final RedisTemplate<String, String> redisTemplate; // Redis 와 데이터를 주고받는 템플릿

    @Transactional
    public void signup(SignupRequest request) {
        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setName(request.getName());
        member.setRole("ROLE_USER");
        memberMapper.insertMember(member);
    }

    // 로그인 처리를 수행하고 정상 사용자라면 JWT 토큰 세트를 발급
    @Transactional
    public TokenResponse login(String username, String password) {
        // 1. 사용자가 입력한 아이디와 비밀번호를 담은 임시 인증용 토큰 객체를 만듦
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 매니저가 DB 정보와 일치하는지 검증한다. 통과하면 권한이 포함된 진짜 인증 객체가 나옴
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 검증된 정보를 바탕으로 액세스 토큰과 리프레시 토큰을 각각 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 4. 리프레시 토큰은 나중에 비교하기 위해 Redis 에 저장, 키는 "RT:username" 형태로 만듦
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                refreshToken,
                jwtTokenProvider.getExpiration(refreshToken),
                TimeUnit.MILLISECONDS
        );

        // 5. 완성된 두 개의 토큰을 컨트롤러로 반환
        return new TokenResponse(accessToken, refreshToken);
    }

    // 로그아웃 처리를 수행하여 기존 토큰들을 무력화
    @Transactional
    public void logout(String accessToken, String username) {
        // 1. Redis 에 저장된 이 사용자의 리프레시 토큰을 찾아 삭제 (더 이상 새 토큰 재발급을 막음)
        if (redisTemplate.opsForValue().get("RT:" + username) != null) {
            redisTemplate.delete("RT:" + username);
        }

        // 2. 현재 사용 중인 액세스 토큰의 남은 유효 시간을 계산하여 알아냄
        Long expiration = jwtTokenProvider.getExpiration(accessToken);

        // 3. 해당 액세스 토큰을 Redis 에 "logout" 이라는 값과 함께 저장해 블랙리스트
        // (이후 필터에서 이 토큰을 들고 오면 로그아웃된 것으로 간주해 막아버린다.)
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // 만료된 액세스 토큰을 대체하기 위해 리프레시 토큰을 이용하여 새 토큰으로 재발급
    @Transactional
    public TokenResponse reissue(String refreshToken) {
        // 1. 클라이언트가 보낸 리프레시 토큰 자체가 유효한지 1차로 검증한다.
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2. 토큰에서 사용자 정보를 꺼낸 뒤, 해당 사용자의 이름으로 Redis 에 저장되어 있던 진짜 리프레시 토큰을 가져옴
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String redisRefreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // 3. 클라이언트가 보낸 토큰과 Redis 에 저장된 토큰이 똑같은지 비교
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new RuntimeException("Refresh token does not match");
        }

        // 4. 모두 정상적으로 확인되면 새로운 액세스 토큰과 리프레시 토큰 만듦
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 5. 새로 만든 리프레시 토큰으로 Redis 의 기존 데이터를 덮어씀
        redisTemplate.opsForValue().set(
                "RT:" + authentication.getName(),
                newRefreshToken,
                jwtTokenProvider.getExpiration(newRefreshToken),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void updateMember(String username, UpdateMemberRequest request) {
        Member member = new Member();
        member.setUsername(username);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        member.setName(request.getName());

        memberMapper.updateMember(member);
    }
}