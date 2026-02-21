package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Account;
import jpabook.mgpractice.mapper.AccountMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // 가짜 객체를 만드는 Mockito
public class AccountServiceTest {

    @Mock // 가짜 매퍼 객체를 생성. 실제 DB 타지 않음
    private AccountMapper accountMapper;

    @InjectMocks // 생성된 가짜 매퍼를 AccountService에 주입
    private AccountService accountService;

    @Test
    @DisplayName("계좌 개설 로직이 정상적으로 수행되면 생성된 ID 반환")
    void createdAccountSuccess(){
        // given
        String username = "test";
        Long balance = 50000L;

        // 매퍼 insertAccount 호출 시 가짜로 ID를 100L로 세팅해준다고 가정함
        // 실제 DB 안쓰기에 가능
        Account mockAccount = Account.builder().accountId(100L).build();

        // when
        Long generatedId = accountService.createAccount(username, balance);

        // then
        // 매퍼의 insertAccount 메서드가 실제 호출되었는지 검증
        verify(accountMapper).insertAccount(any(Account.class));
        System.out.println("서비스 호출 완료");

    }

}
