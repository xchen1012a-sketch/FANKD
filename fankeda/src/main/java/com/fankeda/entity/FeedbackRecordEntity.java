package com.fankeda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_records")
public class FeedbackRecordEntity {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "stall_id", nullable = false)
    private Long stallId;

    @Column(nullable = false)
    private boolean accurate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected FeedbackRecordEntity() {
    }

    public FeedbackRecordEntity(Long id, Long userId, Long stallId, boolean accurate, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.stallId = stallId;
        this.accurate = accurate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getStallId() {
        return stallId;
    }

    public boolean isAccurate() {
        return accurate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
