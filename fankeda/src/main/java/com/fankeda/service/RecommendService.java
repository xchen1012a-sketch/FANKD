package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.RecommendResponse;
import com.fankeda.dto.RecommendStallResponse;
import com.fankeda.model.QueueSnapshot;
import com.fankeda.model.Stall;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RecommendService {

    private static final double RUSH_LIMIT_MINUTES = 14.0;
    private static final Weights NORMAL_WEIGHTS = new Weights(0.35, 0.20, 0.18, 0.27);
    private static final Weights RUSH_WEIGHTS = new Weights(0.52, 0.22, 0.18, 0.08);

    private final FankedaStore store;
    private final ResponseMapper mapper;

    public RecommendService(FankedaStore store, ResponseMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    public RecommendResponse recommend(boolean rushMode) {
        Weights weights = rushMode ? RUSH_WEIGHTS : NORMAL_WEIGHTS;
        List<RecommendStallResponse> scored = store.stalls().stream()
            .map(stall -> score(stall, store.latestSnapshot(stall.id()), weights))
            .filter(stall -> !rushMode || stall.totalTime() <= RUSH_LIMIT_MINUTES)
            .sorted(Comparator.comparingDouble(RecommendStallResponse::score).reversed())
            .toList();

        if (scored.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "没有符合条件的窗口");
        }
        return new RecommendResponse(scored.get(0), scored);
    }

    private RecommendStallResponse score(Stall stall, QueueSnapshot snapshot, Weights weights) {
        ResponseMapper.Timing timing = mapper.timing(stall, snapshot.queueCount());
        double timeScore = clamp(100 - timing.totalTime() * 6);
        double congestionScore = clamp(100 - (snapshot.queueCount() / 26.0) * 100);
        double distanceScore = clamp(100 - stall.distance() / 2.8);
        double tasteScore = clamp(stall.rating() * 20);
        double score = timeScore * weights.time()
            + congestionScore * weights.congestion()
            + distanceScore * weights.distance()
            + tasteScore * weights.taste();
        return new RecommendStallResponse(
            stall.id(),
            stall.name(),
            mapper.round(score),
            mapper.level(snapshot.queueCount()),
            mapper.round(timing.totalTime()),
            mapper.round(timing.waitTime()),
            mapper.round(timing.walkTime()),
            mapper.round(timing.prepTime()),
            snapshot.queueCount(),
            stall.distance(),
            stall.rating()
        );
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(100, value));
    }

    private record Weights(double time, double congestion, double distance, double taste) {
    }
}
