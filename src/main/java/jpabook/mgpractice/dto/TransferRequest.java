package jpabook.mgpractice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransferRequest {

    @Schema(description = "출금 계좌 ID", example = "1")
    @NotNull(message = "출금 계좌 번호는 필수입니다.")
    private Long fromAccountId;

    @Schema(description = "임금 계좌 ID", example = "2")
    @NotNull(message = "입금 계좌 번호는 필수입니다.")
    private Long toAccountId;

    @Schema(description = "이체 금액", example = "100000")
    @NotNull(message = "이체 금액은 필수입니다.")
    @Min(value = 1, message = "이체 금액은 1원 이상이어야 합니다.")
    private Long amount;
}