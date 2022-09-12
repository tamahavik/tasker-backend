package org.project.tasker.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.project.tasker.model.dto.LoginRequest;
import org.project.tasker.model.dto.LoginResponse;
import org.project.tasker.model.dto.RegisterRequest;
import org.project.tasker.model.dto.MessageResponse;
import org.project.tasker.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoint")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/login",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LoginResponse> doLogin(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authenticationService.doLogin(request);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PostMapping(value = "/register")
    @ResponseBody
    public ResponseEntity<MessageResponse> doRegister(@RequestBody @Valid RegisterRequest request) {
        MessageResponse response = authenticationService.doRegister(request);
        return ResponseEntity.status(CREATED)
                .contentType(APPLICATION_JSON)
                .body(response);
    }



}
