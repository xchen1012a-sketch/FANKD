package com.fankeda.dto;

import java.time.LocalDateTime;

public record FeedbackResponse(
    Long id,
    Long userId,
    Long stallId,
    boolean isAccurate,
    double adjustedServeSpeed,
    LocalDateTime createdAt
) {
}
