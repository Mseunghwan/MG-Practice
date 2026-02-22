package jpabook.mgpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jpabook.mgpractice.dto.LoginRequest;
import jpabook.mgpractice.dto.SignupRequest;
import jpabook.mgpractice.dto.TokenResponse;
import jpabook.mgpractice.dto.UpdateMemberRequest;
import jpabook.mgpractice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 API", description = "회원가입, 로그인, 로그아웃 등 사용자 인증 및 관리를 담당하는 API다.")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록한다. DTO를 통해 데이터를 받는다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        memberService.signup(request);
        return ResponseEntity.ok("Signup successful");
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하여 정상 회원임이 확인되면 JWT 액세스 토큰과 리프레시 토큰을 발급받는다.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 상태에서 요청만 보내면 자동으로 헤더의 토큰을 추출하여 블랙리스트 처리한다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, Authentication authentication) {
        String token = resolveToken(request);
        if (token != null) {
            memberService.logout(token, authentication.getName());
        }
        return ResponseEntity.ok("Logout successful");
    }

    @Operation(summary = "토큰 재발급", description = "만료된 액세스 토큰 대신 유효한 리프레시 토큰을 헤더로 전달하여 새로운 토큰 쌍을 발급받는다.")
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        TokenResponse tokenResponse = memberService.reissue(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "회원 정보 수정", description = "현재 로그인된 사용자의 정보를 수정한다.")
    @PutMapping("/update")
    public ResponseEntity<String> updateMember(@RequestBody UpdateMemberRequest request, Authentication authentication) {
        memberService.updateMember(authentication.getName(), request);
        return ResponseEntity.ok("Update successful");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}