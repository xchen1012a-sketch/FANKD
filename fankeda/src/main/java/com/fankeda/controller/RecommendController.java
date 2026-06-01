package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.RecommendResponse;
import com.fankeda.service.RecommendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @GetMapping
    ApiResponse<RecommendResponse> recommend(@RequestParam(defaultValue = "false") boolean rushMode) {
        return ApiResponse.ok(recommendService.recommend(rushMode));
    }
}
