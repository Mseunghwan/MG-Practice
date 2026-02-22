package jpabook.mgpractice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "사용자 닉네임", example = "mseunghwan123")
    @NotBlank(message = "username은 필수입니다.")
    String username;

    @Schema(description = "사용자 비밀번호", example = "1234")
    @NotBlank(message = "password은 필수입니다.")
    String password;
}
