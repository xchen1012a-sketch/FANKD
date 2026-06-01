package com.fankeda.model;

import java.time.LocalDateTime;

public record QueueSnapshot(
    Long id,
    Long stallId,
    int queueCount,
    Long reporterId,
    LocalDateTime createdAt
) {
}
