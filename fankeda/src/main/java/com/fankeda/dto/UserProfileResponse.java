package com.fankeda.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
    Long id,
    String openid,
    String nickname,
    String classEndTime,
    int reportCount,
    int timeSaved,
    LocalDateTime createdAt
) {
}
