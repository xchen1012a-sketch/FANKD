package com.fankeda.dto;

import java.time.LocalDateTime;

public record AdminFeedbackResponse(
    Long id,
    Long userId,
    String userNickname,
    Long stallId,
    String stallName,
    boolean isAccurate,
    LocalDateTime createdAt
) {
}
