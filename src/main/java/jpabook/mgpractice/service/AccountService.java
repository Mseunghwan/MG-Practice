package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Account;
import jpabook.mgpractice.mapper.AccountMapper;
import jpabook.mgpractice.mapper.TransactionHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final TransactionHistoryService transactionHistoryService;

    // 신규 계좌 개설
    @Transactional
    public Long createAccount(String username, Long initialDeposit){
        Account account = Account.builder()
                .username(username)
                .balance(initialDeposit)
                .build();

        accountMapper.insertAccount(account);

        return account.getAccountId();
    }

    // 회원 보유 계좌 목록 조회
    @Transactional(readOnly = true)
    public List<Account> getMyAccount(String username){
        return accountMapper.findAccountsByUsername(username);
    }

    // 계좌 이체 로직
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Long amount){

        try {

            // 1. 출금계좌 조회 및 잔액 검증
            Account fromAccount = accountMapper.findByAccountId(fromAccountId);
            if (fromAccount == null) {
                throw new IllegalArgumentException("출금할 계좌를 찾을 수 없습니다.");
            }
            if (fromAccount.getBalance() < amount) {
                throw new IllegalArgumentException("잔액이 부족합니다.");
            }

            // 2. 입금 계좌 조회
            Account toAccount = accountMapper.findByAccountId(toAccountId);
            if(toAccount == null){
                throw new IllegalArgumentException("입금할 계좌를 찾을 수 없습니다.");
            }

            // 3. 잔액 업데이트
            accountMapper.updateBalance(fromAccountId, -amount);
            accountMapper.updateBalance(toAccountId, amount);

            // 4. 성공 시 이력 기록
            transactionHistoryService.insertHistory(fromAccountId, toAccountId, amount, "SUCCESS");

        } catch (Exception e) {
            // 5. 실패 시 이력 기록
            // REQUIRES_NEW로 인해 메인 트랜잭션이 롤백되더라도 이 코드는 데이터베이스에 커밋
            transactionHistoryService.insertHistory(fromAccountId, toAccountId, amount, "FAIL");

            // 데이터베이스 롤백을 트리거하기 위해 예외를 다시 던짐
            throw e;
        }
    }

}
