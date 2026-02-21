package jpabook.mgpractice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Account {
    private Long accountId;
    private String username;
    private long balance;
}
