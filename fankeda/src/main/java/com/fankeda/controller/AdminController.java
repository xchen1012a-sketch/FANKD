package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.AdminFeedbackResponse;
import com.fankeda.dto.AdminStallRequest;
import com.fankeda.dto.StallResponse;
import com.fankeda.dto.UserProfileResponse;
import com.fankeda.service.AdminService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/stalls")
    ApiResponse<StallResponse> createStall(@RequestBody AdminStallRequest request) {
        return ApiResponse.ok(adminService.createStall(request));
    }

    @PutMapping("/stalls/{stallId}")
    ApiResponse<StallResponse> updateStall(
        @PathVariable Long stallId,
        @RequestBody AdminStallRequest request
    ) {
        return ApiResponse.ok(adminService.updateStall(stallId, request));
    }

    @DeleteMapping("/stalls/{stallId}")
    ApiResponse<Boolean> deleteStall(@PathVariable Long stallId) {
        return ApiResponse.ok(adminService.deleteStall(stallId));
    }

    @GetMapping("/feedback")
    ApiResponse<List<AdminFeedbackResponse>> feedback() {
        return ApiResponse.ok(adminService.feedback());
    }

    @GetMapping("/users")
    ApiResponse<List<UserProfileResponse>> users() {
        return ApiResponse.ok(adminService.users());
    }
}
