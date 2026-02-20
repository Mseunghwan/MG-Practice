package jpabook.mgpractice.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Member {
    private Long memberId;
    private String username;
    private String password;
    private String name;
    private String role;
    private LocalDateTime createdAt;
}
