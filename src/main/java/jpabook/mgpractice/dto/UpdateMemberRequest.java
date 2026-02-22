package jpabook.mgpractice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 수정 요청 데이터 객체다.")
public class UpdateMemberRequest {
    @Schema(description = "변경할 비밀번호 (변경하지 않을 경우 비워둔다.)", example = "5678")
    private String password;

    @Schema(description = "변경할 이름", example = "민승환(수정)")
    private String name;
}