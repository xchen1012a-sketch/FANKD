package com.fankeda.model;

import java.time.LocalDateTime;

public record UserProfile(
    Long id,
    String openid,
    String nickname,
    String classEndTime,
    int reportCount,
    int timeSaved,
    LocalDateTime createdAt
) {

    public UserProfile withClassEndTime(String newClassEndTime) {
        return new UserProfile(id, openid, nickname, newClassEndTime, reportCount, timeSaved, createdAt);
    }

    public UserProfile withReportCount(int newReportCount) {
        return new UserProfile(id, openid, nickname, classEndTime, newReportCount, timeSaved, createdAt);
    }
}
