package jpabook.mgpractice.mapper;

import jpabook.mgpractice.domain.Member;
import org.springframework.data.repository.query.Param;

public interface MemberMapper {
    void insertMember(Member member);
    Member findByUsername(@Param("username") String username);
}
