package com.fankeda.service;

import com.fankeda.dto.QueueSnapshotResponse;
import com.fankeda.dto.StallResponse;
import com.fankeda.dto.UserProfileResponse;
import com.fankeda.model.QueueSnapshot;
import com.fankeda.model.Stall;
import com.fankeda.model.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    private static final double WALK_SPEED_METERS_PER_MINUTE = 80.0;

    private final FankedaStore store;

    public ResponseMapper(FankedaStore store) {
        this.store = store;
    }

    public StallResponse toStallResponse(Stall stall, QueueSnapshot snapshot) {
        Timing timing = timing(stall, snapshot.queueCount());
        return new StallResponse(
            stall.id(),
            stall.name(),
            stall.type(),
            stall.posX(),
            stall.posY(),
            stall.serveSpeed(),
            stall.distance(),
            stall.avgPrep(),
            stall.rating(),
            snapshot.queueCount(),
            round(timing.waitTime()),
            round(timing.walkTime()),
            round(timing.totalTime()),
            level(snapshot.queueCount())
        );
    }

    public QueueSnapshotResponse toQueueSnapshotResponse(QueueSnapshot snapshot) {
        Stall stall = store.requireStall(snapshot.stallId());
        return new QueueSnapshotResponse(
            snapshot.id(),
            snapshot.stallId(),
            stall.name(),
            snapshot.queueCount(),
            snapshot.reporterId(),
            snapshot.createdAt()
        );
    }

    public UserProfileResponse toProfileResponse(UserProfile user) {
        return new UserProfileResponse(
            user.id(),
            user.openid(),
            user.nickname(),
            user.classEndTime(),
            user.reportCount(),
            user.timeSaved(),
            user.createdAt()
        );
    }

    public Timing timing(Stall stall, int queueCount) {
        double waitTime = queueCount / stall.serveSpeed();
        double walkTime = stall.distance() / WALK_SPEED_METERS_PER_MINUTE;
        double totalTime = waitTime + walkTime + stall.avgPrep();
        return new Timing(waitTime, walkTime, stall.avgPrep(), totalTime);
    }

    public String level(int queueCount) {
        if (queueCount <= 7) {
            return "人少";
        }
        if (queueCount <= 12) {
            return "一般";
        }
        if (queueCount <= 18) {
            return "较挤";
        }
        return "爆满";
    }

    public double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record Timing(double waitTime, double walkTime, double prepTime, double totalTime) {
    }
}
