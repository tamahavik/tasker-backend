package org.hatama.tasker.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.hatama.tasker.model.dto.AppUsersRequest;
import org.hatama.tasker.model.dto.AppUsersResponse;
import org.hatama.tasker.model.dto.PagingRequest;
import org.hatama.tasker.service.AppUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Users", description = "Application user endpoint")
public class AppUsersController {

    private final AppUsersService appUsersService;

    public AppUsersController(AppUsersService appUsersService) {
        this.appUsersService = appUsersService;
    }

    @PostMapping(value = "/users",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AppUsersResponse>> getAllUsers(@RequestBody @Valid PagingRequest page) {
        List<AppUsersResponse> responses = appUsersService.findAllUser(page);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(responses);
    }

    @GetMapping(value = "/user",
            produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUsersResponse> getUserById(@RequestParam String id) {
        AppUsersResponse response = appUsersService.findUserById(id);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PostMapping(value = "/user",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUsersResponse> createUser(@RequestBody @Valid AppUsersRequest request) {
        AppUsersResponse response = appUsersService.createUser(request);
        return ResponseEntity.status(CREATED)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @PutMapping(value = "/user",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUsersResponse> updateUser(@RequestParam String id,
                                                       @RequestBody @Valid AppUsersRequest request) {
        AppUsersResponse response = appUsersService.updateUser(id, request);
        return ResponseEntity.status(OK)
                .contentType(APPLICATION_JSON)
                .body(response);
    }

    @DeleteMapping(value = "/user")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@RequestParam String id) {
        String response = appUsersService.deleteUser(id);
        return ResponseEntity.status(NO_CONTENT)
                .body(response);
    }
}
