package jpabook.mgpractice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateRequest {

    @Schema(description = "사용자 닉네임", example = "Mseunghwan")
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @Schema(description = "초기 입금액", example = "20000000")
    @NotNull(message = "초기 입금액은 필수입니다.")
    @Min(value = 0, message = "초기 입금액은 0원 이상이어야 합니다.")
    private Long initialDeposit;

}
