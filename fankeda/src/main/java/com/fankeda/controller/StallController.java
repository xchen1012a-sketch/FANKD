package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.StallResponse;
import com.fankeda.service.StallService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stalls")
public class StallController {

    private final StallService stallService;

    public StallController(StallService stallService) {
        this.stallService = stallService;
    }

    @GetMapping
    ApiResponse<List<StallResponse>> list() {
        return ApiResponse.ok(stallService.listStalls());
    }
}
