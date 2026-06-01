package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.FeedbackRequest;
import com.fankeda.dto.FeedbackResponse;
import com.fankeda.service.FeedbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    ApiResponse<FeedbackResponse> create(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody FeedbackRequest request
    ) {
        return ApiResponse.ok(feedbackService.create(authorization, request));
    }
}
