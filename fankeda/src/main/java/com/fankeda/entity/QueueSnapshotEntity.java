package com.fankeda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_snapshots")
public class QueueSnapshotEntity {

    @Id
    private Long id;

    @Column(name = "stall_id", nullable = false)
    private Long stallId;

    @Column(name = "queue_count", nullable = false)
    private int queueCount;

    @Column(name = "reporter_id")
    private Long reporterId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected QueueSnapshotEntity() {
    }

    public QueueSnapshotEntity(Long id, Long stallId, int queueCount, Long reporterId, LocalDateTime createdAt) {
        this.id = id;
        this.stallId = stallId;
        this.queueCount = queueCount;
        this.reporterId = reporterId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getStallId() {
        return stallId;
    }

    public int getQueueCount() {
        return queueCount;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
