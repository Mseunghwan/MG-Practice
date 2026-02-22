package jpabook.mgpractice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 데이터 객체다.")
public class SignupRequest {
    @Schema(description = "사용자 고유 아이디", example = "Mseunghwan")
    private String username;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "이름", example = "민승환")
    private String name;
}