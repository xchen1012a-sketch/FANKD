package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.FeedbackRequest;
import com.fankeda.dto.FeedbackResponse;
import com.fankeda.model.FeedbackRecord;
import com.fankeda.model.Stall;
import com.fankeda.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private static final double INACCURATE_SPEED_DELTA = -0.12;

    private final FankedaStore store;
    private final AuthService authService;

    public FeedbackService(FankedaStore store, AuthService authService) {
        this.store = store;
        this.authService = authService;
    }

    public FeedbackResponse create(String authorization, FeedbackRequest request) {
        validate(request);
        UserProfile user = authService.requireUser(authorization);
        FeedbackRecord feedback = store.addFeedback(user.id(), request.stallId(), request.isAccurate());
        Stall stall = request.isAccurate()
            ? store.requireStall(request.stallId())
            : store.adjustServeSpeed(request.stallId(), INACCURATE_SPEED_DELTA);
        return new FeedbackResponse(
            feedback.id(),
            feedback.userId(),
            feedback.stallId(),
            feedback.accurate(),
            stall.serveSpeed(),
            feedback.createdAt()
        );
    }

    private void validate(FeedbackRequest request) {
        if (request == null || request.stallId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "stallId不能为空");
        }
        if (request.isAccurate() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "isAccurate不能为空");
        }
    }
}
