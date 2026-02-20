package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Member;
import jpabook.mgpractice.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    // 데이터가 변하는 작업(INSERT)이므로 트랜잭션을 걸어줍니다.
    // 만약 가입 처리 중 에러가 발생하면 저장된 정보가 자동으로 롤백됩니다.
    @Transactional
    public void signup(Member member) {
        // 1. 사용자가 입력한 평문 비밀번호를 BCrypt로 암호화하여 다시 세팅합니다.
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        // 2. 권한이 비어있다면 기본 유저 권한을 부여합니다.
        if (member.getRole() == null) {
            member.setRole("ROLE_USER");
        }

        // 3. MyBatis 매퍼를 호출해 오라클 DB에 최종 저장합니다.
        memberMapper.insertMember(member);
    }
}