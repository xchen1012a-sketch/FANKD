package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.UpdateProfileRequest;
import com.fankeda.dto.UserProfileResponse;
import com.fankeda.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AuthService authService;
    private final FankedaStore store;
    private final ResponseMapper mapper;

    public UserService(AuthService authService, FankedaStore store, ResponseMapper mapper) {
        this.authService = authService;
        this.store = store;
        this.mapper = mapper;
    }

    public UserProfileResponse profile(String authorization) {
        return mapper.toProfileResponse(authService.requireUser(authorization));
    }

    public UserProfileResponse update(String authorization, UpdateProfileRequest request) {
        if (request == null || request.classEndTime() == null || request.classEndTime().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "classEndTime不能为空");
        }
        String classEndTime = request.classEndTime().trim();
        if (!classEndTime.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "classEndTime格式应为HH:mm");
        }
        UserProfile user = authService.requireUser(authorization);
        return mapper.toProfileResponse(store.updateClassEndTime(user, classEndTime));
    }
}
