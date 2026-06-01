package com.fankeda.model;

import java.time.LocalDateTime;

public record FeedbackRecord(
    Long id,
    Long userId,
    Long stallId,
    boolean accurate,
    LocalDateTime createdAt
) {
}
