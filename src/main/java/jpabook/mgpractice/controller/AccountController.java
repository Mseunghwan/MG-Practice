package jpabook.mgpractice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jpabook.mgpractice.domain.Account;
import jpabook.mgpractice.dto.AccountCreateRequest;
import jpabook.mgpractice.dto.AccountResponse;
import jpabook.mgpractice.dto.TransferRequest;
import jpabook.mgpractice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "계좌 API", description = "계좌 개설, 조회 및 이체 관련 API")
@Controller
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 신규 계좌 개설 API
    @Operation(summary = "신규 계좌 개설", description = "사용자 이름과 초기 입금액을 입력받아 새로운 계좌를 개설합니다.")
    @PostMapping
    public ResponseEntity<Long> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        Long accountId = accountService.createAccount(request.getUsername(), request.getInitialDeposit());

        return ResponseEntity.ok(accountId);
    }

    // 보유 계좌 목록 조회 API
    @Operation(summary = "보유 계좌 목록 조회", description = "사용자 이름을 받아 보유 계좌를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@RequestParam String username) {
        List<Account> accounts = accountService.getMyAccount(username);

        List<AccountResponse> responseList = accounts
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    // 계좌 이체 API
    @Operation(summary = "계좌 이체", description = "인출 계좌 정보, 입금 계좌 정보를 받아 이체 금액만큼 이체합니다.")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferAccount(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        return ResponseEntity.ok("이체가 성공적으로 완료되었습니다.");
    }

}
