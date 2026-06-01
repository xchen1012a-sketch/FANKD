package com.fankeda.dto;

import java.time.LocalDateTime;

public record QueueSnapshotResponse(
    Long id,
    Long stallId,
    String stallName,
    int queueCount,
    Long reporterId,
    LocalDateTime createdAt
) {
}
