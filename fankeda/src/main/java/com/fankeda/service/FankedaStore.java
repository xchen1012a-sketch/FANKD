package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.entity.AuthTokenEntity;
import com.fankeda.entity.FeedbackRecordEntity;
import com.fankeda.entity.QueueSnapshotEntity;
import com.fankeda.entity.StallEntity;
import com.fankeda.entity.UserProfileEntity;
import com.fankeda.model.FeedbackRecord;
import com.fankeda.model.QueueSnapshot;
import com.fankeda.model.Stall;
import com.fankeda.model.UserProfile;
import com.fankeda.repository.AuthTokenRepository;
import com.fankeda.repository.FeedbackRecordRepository;
import com.fankeda.repository.QueueSnapshotRepository;
import com.fankeda.repository.StallRepository;
import com.fankeda.repository.UserProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FankedaStore {

    private final StallRepository stallRepository;
    private final QueueSnapshotRepository queueSnapshotRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthTokenRepository authTokenRepository;
    private final FeedbackRecordRepository feedbackRecordRepository;

    public FankedaStore(
        StallRepository stallRepository,
        QueueSnapshotRepository queueSnapshotRepository,
        UserProfileRepository userProfileRepository,
        AuthTokenRepository authTokenRepository,
        FeedbackRecordRepository feedbackRecordRepository
    ) {
        this.stallRepository = stallRepository;
        this.queueSnapshotRepository = queueSnapshotRepository;
        this.userProfileRepository = userProfileRepository;
        this.authTokenRepository = authTokenRepository;
        this.feedbackRecordRepository = feedbackRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<Stall> stalls() {
        return stallRepository.findAllByOrderByIdAsc().stream()
            .map(this::toStall)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfile> users() {
        return userProfileRepository.findAllByOrderByIdAsc().stream()
            .map(this::toUserProfile)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackRecord> feedbackRecords() {
        return feedbackRecordRepository.findAllByOrderByIdAsc().stream()
            .map(this::toFeedbackRecord)
            .toList();
    }

    @Transactional(readOnly = true)
    public Stall requireStall(Long stallId) {
        if (stallId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "stallId不能为空");
        }
        return stallRepository.findById(stallId)
            .map(this::toStall)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "窗口不存在"));
    }

    @Transactional
    public Stall addStall(Stall stall) {
        Long stallId = nextId(stallRepository.findMaxId());
        StallEntity saved = stallRepository.save(new StallEntity(
            stallId,
            stall.name(),
            stall.type(),
            stall.posX(),
            stall.posY(),
            stall.serveSpeed(),
            stall.distance(),
            stall.avgPrep(),
            stall.rating()
        ));
        return toStall(saved);
    }

    @Transactional
    public Stall updateStall(Long stallId, Stall stall) {
        requireStall(stallId);
        StallEntity saved = stallRepository.save(new StallEntity(
            stallId,
            stall.name(),
            stall.type(),
            stall.posX(),
            stall.posY(),
            stall.serveSpeed(),
            stall.distance(),
            stall.avgPrep(),
            stall.rating()
        ));
        return toStall(saved);
    }

    @Transactional
    public void deleteStall(Long stallId) {
        requireStall(stallId);
        stallRepository.deleteById(stallId);
    }

    @Transactional(readOnly = true)
    public QueueSnapshot latestSnapshot(Long stallId) {
        return queueSnapshotRepository.findTopByStallIdOrderByCreatedAtDescIdDesc(stallId)
            .map(this::toQueueSnapshot)
            .orElseGet(() -> new QueueSnapshot(0L, stallId, 0, null, LocalDateTime.now()));
    }

    @Transactional
    public QueueSnapshot addQueueSnapshot(Long stallId, int queueCount, Long reporterId) {
        requireStall(stallId);
        QueueSnapshotEntity saved = queueSnapshotRepository.save(new QueueSnapshotEntity(
            nextId(queueSnapshotRepository.findMaxId()),
            stallId,
            queueCount,
            reporterId,
            LocalDateTime.now()
        ));
        if (reporterId != null) {
            incrementReportCount(reporterId);
        }
        return toQueueSnapshot(saved);
    }

    @Transactional
    public LoginSession login(String code) {
        String openid = "mock-openid-" + code;
        UserProfileEntity user = userProfileRepository.findByOpenid(openid)
            .orElseGet(() -> userProfileRepository.save(new UserProfileEntity(
                nextId(userProfileRepository.findMaxId()),
                openid,
                "饭刻达用户",
                null,
                0,
                0,
                LocalDateTime.now()
            )));
        String token = "demo-token-" + UUID.randomUUID();
        authTokenRepository.save(new AuthTokenEntity(token, user.getId(), LocalDateTime.now()));
        return new LoginSession(token, toUserProfile(user));
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> findUserByToken(String token) {
        return authTokenRepository.findById(token)
            .flatMap(authToken -> userProfileRepository.findById(authToken.getUserId()))
            .map(this::toUserProfile);
    }

    @Transactional(readOnly = true)
    public Optional<UserProfile> findUserById(Long userId) {
        return userProfileRepository.findById(userId).map(this::toUserProfile);
    }

    @Transactional
    public UserProfile updateClassEndTime(UserProfile user, String classEndTime) {
        UserProfileEntity existing = userProfileRepository.findById(user.id())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录态无效"));
        UserProfileEntity saved = userProfileRepository.save(new UserProfileEntity(
            existing.getId(),
            existing.getOpenid(),
            existing.getNickname(),
            classEndTime,
            existing.getReportCount(),
            existing.getTimeSaved(),
            existing.getCreatedAt()
        ));
        return toUserProfile(saved);
    }

    @Transactional
    public FeedbackRecord addFeedback(Long userId, Long stallId, boolean accurate) {
        requireStall(stallId);
        FeedbackRecordEntity saved = feedbackRecordRepository.save(new FeedbackRecordEntity(
            nextId(feedbackRecordRepository.findMaxId()),
            userId,
            stallId,
            accurate,
            LocalDateTime.now()
        ));
        return toFeedbackRecord(saved);
    }

    @Transactional
    public Stall adjustServeSpeed(Long stallId, double delta) {
        Stall stall = requireStall(stallId);
        double adjusted = Math.max(0.2, round2(stall.serveSpeed() + delta));
        StallEntity saved = stallRepository.save(new StallEntity(
            stall.id(),
            stall.name(),
            stall.type(),
            stall.posX(),
            stall.posY(),
            adjusted,
            stall.distance(),
            stall.avgPrep(),
            stall.rating()
        ));
        return toStall(saved);
    }

    private void incrementReportCount(Long userId) {
        userProfileRepository.findById(userId).ifPresent(user -> userProfileRepository.save(new UserProfileEntity(
            user.getId(),
            user.getOpenid(),
            user.getNickname(),
            user.getClassEndTime(),
            user.getReportCount() + 1,
            user.getTimeSaved(),
            user.getCreatedAt()
        )));
    }

    private Long nextId(Long maxId) {
        return maxId == null ? 1L : maxId + 1L;
    }

    private Stall toStall(StallEntity entity) {
        return new Stall(
            entity.getId(),
            entity.getName(),
            entity.getType(),
            entity.getPosX(),
            entity.getPosY(),
            entity.getServeSpeed(),
            entity.getDistance(),
            entity.getAvgPrep(),
            entity.getRating()
        );
    }

    private QueueSnapshot toQueueSnapshot(QueueSnapshotEntity entity) {
        return new QueueSnapshot(
            entity.getId(),
            entity.getStallId(),
            entity.getQueueCount(),
            entity.getReporterId(),
            entity.getCreatedAt()
        );
    }

    private UserProfile toUserProfile(UserProfileEntity entity) {
        return new UserProfile(
            entity.getId(),
            entity.getOpenid(),
            entity.getNickname(),
            entity.getClassEndTime(),
            entity.getReportCount(),
            entity.getTimeSaved(),
            entity.getCreatedAt()
        );
    }

    private FeedbackRecord toFeedbackRecord(FeedbackRecordEntity entity) {
        return new FeedbackRecord(
            entity.getId(),
            entity.getUserId(),
            entity.getStallId(),
            entity.isAccurate(),
            entity.getCreatedAt()
        );
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record LoginSession(String token, UserProfile user) {
    }
}
