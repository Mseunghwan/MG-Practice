package jpabook.mgpractice.mapper;

import jpabook.mgpractice.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    Member findByUsername(@Param("username") String username);
}
