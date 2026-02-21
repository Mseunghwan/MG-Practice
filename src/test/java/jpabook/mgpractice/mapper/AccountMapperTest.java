package jpabook.mgpractice.mapper;

import jpabook.mgpractice.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest // MyBatis 관련 설정만 불러와 테스트 속도 빠름
public class AccountMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    @DisplayName("계좌 생성 시 시퀀스로 ID가 자동으로 생성되어야 한다")
    void insertAccount(){
        // given
        Account account = Account.builder()
                .username("user1")
                .balance(10000L)
                .build();

        // when
        accountMapper.insertAccount(account);

        // then
        assertThat(account.getAccountId()).isNotNull();
        System.out.println("생성된 계좌 ID : " + account.getAccountId());
    }

    @Test
    @DisplayName("사용자 ID로 계좌 목록을 조회할 수 있다")
    void findByAccountId(){
        // given
        String username = "tester";
        accountMapper.insertAccount(Account.builder().username(username).balance(5000L).build());
        accountMapper.insertAccount(Account.builder().username(username).balance(3000L).build());

        // when
        List<Account> accounts = accountMapper.findAccountsByUsername(username);

        // then
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).getUsername()).isEqualTo(username);
    }

}
