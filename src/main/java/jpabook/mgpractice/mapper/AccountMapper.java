package jpabook.mgpractice.mapper;

import jpabook.mgpractice.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    // 계좌 생성
    void insertAccount(Account account);

    // 특정 계좌 조회
    Account findByAccountId(Long accountId);

    // 사용자 모든 계좌 목록 조회
    List<Account> findAccountByUsername(String username);

    // 잔액 업데이트
    void updateBalance(@Param("accountId") Long accountId, @Param("amount") Long amount);
}
