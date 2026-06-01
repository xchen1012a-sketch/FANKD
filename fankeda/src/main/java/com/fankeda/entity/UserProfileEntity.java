package com.fankeda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserProfileEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String openid;

    @Column(nullable = false, length = 80)
    private String nickname;

    @Column(name = "class_end_time", length = 5)
    private String classEndTime;

    @Column(name = "report_count", nullable = false)
    private int reportCount;

    @Column(name = "time_saved", nullable = false)
    private int timeSaved;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserProfileEntity() {
    }

    public UserProfileEntity(
        Long id,
        String openid,
        String nickname,
        String classEndTime,
        int reportCount,
        int timeSaved,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.openid = openid;
        this.nickname = nickname;
        this.classEndTime = classEndTime;
        this.reportCount = reportCount;
        this.timeSaved = timeSaved;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOpenid() {
        return openid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getClassEndTime() {
        return classEndTime;
    }

    public int getReportCount() {
        return reportCount;
    }

    public int getTimeSaved() {
        return timeSaved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
