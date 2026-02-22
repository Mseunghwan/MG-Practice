package jpabook.mgpractice.dto;

import jpabook.mgpractice.domain.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {
    private Long accountId;
    private String username;
    private long balance;

    // 도메인 객체(Account)를 안전한 응답용 DTO로 변환해주는 편의 메서드
    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .username(account.getUsername())
                .balance(account.getBalance())
                .build();
    }
}
