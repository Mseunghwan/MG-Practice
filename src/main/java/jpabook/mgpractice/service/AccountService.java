package jpabook.mgpractice.service;

import jpabook.mgpractice.domain.Account;
import jpabook.mgpractice.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;

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

}
