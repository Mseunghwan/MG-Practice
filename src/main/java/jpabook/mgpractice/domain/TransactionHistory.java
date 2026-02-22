package jpabook.mgpractice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionHistory {
    private Long historyId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private String status;
    private LocalDateTime createdAt;
}
