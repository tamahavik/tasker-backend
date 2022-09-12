package org.project.tasker.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.project.tasker.model.dto.ChangePasswordRequest;
import org.project.tasker.model.dto.MessageResponse;
import org.project.tasker.model.dto.ResetPasswordResponse;
import org.project.tasker.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Account", description = "Account endpoint")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(value = "/active")
    @ResponseBody
    public ResponseEntity<MessageResponse> activatedAccount(@RequestParam String token) {
        MessageResponse response = accountService.activatedAccount(token);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @GetMapping(value = "/password/forgot")
    @ResponseBody
    public ResponseEntity<MessageResponse> requestResetPassword(@RequestParam String email) {
        MessageResponse response = accountService.requestResetPassword(email);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @GetMapping(value = "/password/reset")
    @ResponseBody
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam String token) {
        ResetPasswordResponse response = accountService.resetPassword(token);
        return ResponseEntity.status(ACCEPTED)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PostMapping(value = "/password/change")
    @ResponseBody
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        MessageResponse response = accountService.changePassword(request);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }
}
