package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.AdminFeedbackResponse;
import com.fankeda.dto.AdminStallRequest;
import com.fankeda.dto.StallResponse;
import com.fankeda.dto.UserProfileResponse;
import com.fankeda.model.FeedbackRecord;
import com.fankeda.model.QueueSnapshot;
import com.fankeda.model.Stall;
import com.fankeda.model.UserProfile;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final FankedaStore store;
    private final ResponseMapper mapper;

    public AdminService(FankedaStore store, ResponseMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    public StallResponse createStall(AdminStallRequest request) {
        Stall stall = store.addStall(toNewStall(request));
        return toStallResponse(stall);
    }

    public StallResponse updateStall(Long stallId, AdminStallRequest request) {
        validateId(stallId);
        Stall stall = store.updateStall(stallId, toExistingStall(stallId, request));
        return toStallResponse(stall);
    }

    public boolean deleteStall(Long stallId) {
        validateId(stallId);
        store.deleteStall(stallId);
        return true;
    }

    public List<AdminFeedbackResponse> feedback() {
        return store.feedbackRecords().stream()
            .map(this::toFeedbackResponse)
            .toList();
    }

    public List<UserProfileResponse> users() {
        return store.users().stream()
            .map(mapper::toProfileResponse)
            .toList();
    }

    private StallResponse toStallResponse(Stall stall) {
        QueueSnapshot snapshot = store.latestSnapshot(stall.id());
        return mapper.toStallResponse(stall, snapshot);
    }

    private AdminFeedbackResponse toFeedbackResponse(FeedbackRecord feedback) {
        UserProfile user = store.findUserById(feedback.userId()).orElse(null);
        Stall stall = store.requireStall(feedback.stallId());
        return new AdminFeedbackResponse(
            feedback.id(),
            feedback.userId(),
            user == null ? "-" : user.nickname(),
            feedback.stallId(),
            stall.name(),
            feedback.accurate(),
            feedback.createdAt()
        );
    }

    private Stall toNewStall(AdminStallRequest request) {
        validate(request);
        return new Stall(
            null,
            request.name().trim(),
            request.type().trim(),
            request.posX(),
            request.posY(),
            request.serveSpeed(),
            request.distance(),
            request.avgPrep(),
            request.rating()
        );
    }

    private Stall toExistingStall(Long stallId, AdminStallRequest request) {
        validate(request);
        return new Stall(
            stallId,
            request.name().trim(),
            request.type().trim(),
            request.posX(),
            request.posY(),
            request.serveSpeed(),
            request.distance(),
            request.avgPrep(),
            request.rating()
        );
    }

    private void validateId(Long stallId) {
        if (stallId == null || stallId <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "stallId不能为空");
        }
    }

    private void validate(AdminStallRequest request) {
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请求体不能为空");
        }
        if (isBlank(request.name())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "窗口名称不能为空");
        }
        if (isBlank(request.type())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "窗口品类不能为空");
        }
        if (request.posX() == null || request.posX() < 0 || request.posX() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "posX必须在0到100之间");
        }
        if (request.posY() == null || request.posY() < 0 || request.posY() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "posY必须在0到100之间");
        }
        if (request.serveSpeed() == null || request.serveSpeed() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "serveSpeed必须大于0");
        }
        if (request.distance() == null || request.distance() < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "distance不能小于0");
        }
        if (request.avgPrep() == null || request.avgPrep() < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "avgPrep不能小于0");
        }
        if (request.rating() == null || request.rating() < 0 || request.rating() > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "rating必须在0到5之间");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
