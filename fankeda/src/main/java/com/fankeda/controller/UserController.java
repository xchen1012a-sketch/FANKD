package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.UpdateProfileRequest;
import com.fankeda.dto.UserProfileResponse;
import com.fankeda.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    ApiResponse<UserProfileResponse> profile(
        @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.ok(userService.profile(authorization));
    }

    @PutMapping
    ApiResponse<UserProfileResponse> update(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody UpdateProfileRequest request
    ) {
        return ApiResponse.ok(userService.update(authorization, request));
    }
}
