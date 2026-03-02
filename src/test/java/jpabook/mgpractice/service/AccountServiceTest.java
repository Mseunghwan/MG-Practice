package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Account;
import jpabook.mgpractice.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate; // 회원 데이터 사전 삽입을 위해 추가

    private Long fromAccountId;
    private Long toAccountId;

    @BeforeEach
    void setUp() {
        // 외래 키(FK_ACCOUNT_MEMBER) 제약 조건을 통과하기 위해
        // 테스트에 사용될 회원(sender, receiver, Mserna)을 Member 테이블에 미리 삽입합니다.
        jdbcTemplate.update("INSERT INTO member (username, password, name, role) VALUES (?, ?, ?, ?)", "sender", "1234", "Sender", "ROLE_USER");
        jdbcTemplate.update("INSERT INTO member (username, password, name, role) VALUES (?, ?, ?, ?)", "receiver", "1234", "Receiver", "ROLE_USER");
        jdbcTemplate.update("INSERT INTO member (username, password, name, role) VALUES (?, ?, ?, ?)", "Mserna", "1234", "Mserna", "ROLE_USER");

        // 테스트 전 송금/수취용 계좌 2개를 미리 생성
        fromAccountId = accountService.createAccount("sender", 10000L); // 출금 계좌 잔액 10,000원
        toAccountId = accountService.createAccount("receiver", 0L);     // 입금 계좌 잔액 0원
    }

    @Test
    @DisplayName("계좌 개설 로직이 정상적으로 수행되면 생성된 ID를 반환")
    void createAccount() {

        // given
        String username = "Mserna";
        Long balance = 50000L;

        // when
        Long generatedId = accountService.createAccount(username, balance);

        // then
        Account savedAccount = accountMapper.findByAccountId(generatedId);
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getUsername()).isEqualTo(username);
        assertThat(savedAccount.getBalance()).isEqualTo(balance);
        log.info("서비스 호출 및 DB 저장 완료. 발급된 ID : {}", generatedId);
    }

    @Test
    @DisplayName("정상 이체 : 잔액이 올바르게 차감 및 증액 되어야 함")
    void transfer_success() {
        // given
        Long transferAmount = 3000L;

        // when
        accountService.transfer(fromAccountId, toAccountId, transferAmount);

        // then
        Account fromAccount = accountMapper.findByAccountId(fromAccountId);
        Account toAccount = accountMapper.findByAccountId(toAccountId);

        assertThat(fromAccount.getBalance()).isEqualTo(7000L);
        assertThat(toAccount.getBalance()).isEqualTo(3000L);

        log.info("정상 이체 완료. 송금인 잔액: {}, 수취인 잔액: {}", fromAccount.getBalance(), toAccount.getBalance());
    }

    @Test
    @DisplayName("이체 실패 (잔액 부족): 메인 트랜잭션은 롤백되어 잔액이 유지되어야 합니다.")
    void transfer_fail_insufficient_balance() {
        // given
        Long overAmount = 20000L; // 잔액보다 큰 금액 시도

        // when & then
        assertThatThrownBy(() -> accountService.transfer(fromAccountId, toAccountId, overAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔액이 부족합니다.");

        // 예외 발생으로 롤백 검증
        Account fromAccount = accountMapper.findByAccountId(fromAccountId);
        Account toAccount = accountMapper.findByAccountId(toAccountId);

        assertThat(fromAccount.getBalance()).isEqualTo(10000L);
        assertThat(toAccount.getBalance()).isEqualTo(0L);

        log.info("잔액 부족 예외 발생 및 롤백 검증 완료. 송금인 잔액 유지: {}", fromAccount.getBalance());
    }


}
