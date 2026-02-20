package jpabook.mgpractice.security;

import jpabook.mgpractice.domain.Member;
import jpabook.mgpractice.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자가 로그인을 시도할 때, 스프링 시큐리티가 "이 유저가 DB에 있는지 확인해줘"라고
 * 요청하는 인터페이스(UserDetailsService)의 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper; // MyBatis 매퍼를 주입받아 DB 조회를 수행합니다.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. MyBatis를 이용해 오라클 DB에서 회원 정보를 조회합니다.
        Member member = memberMapper.findByUsername(username);

        if (member == null) {
            // 회원이 없으면 시큐리티가 제공하는 예외를 던집니다.
            throw new UsernameNotFoundException("회원을 찾을 수 없습니다: " + username);
        }

        // 2. DB에서 찾은 회원 정보를 스프링 시큐리티가 이해할 수 있는 UserDetails 객체로 변환하여 반환합니다.
        // 입력받은 비밀번호와 DB의 암호화된 비밀번호가 일치하는지 비교하는 작업은 이 이후에 시큐리티가 알아서 처리해 줍니다.
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                // DB에 'ROLE_USER'로 저장되어 있다면, 'ROLE_' 접두사를 떼고 넣어주어야 권한 인식이 정상 작동합니다.
                .roles(member.getRole() != null ? member.getRole().replace("ROLE_", "") : "USER")
                .build();
    }
}