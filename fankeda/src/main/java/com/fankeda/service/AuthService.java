package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.LoginRequest;
import com.fankeda.dto.LoginResponse;
import com.fankeda.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final FankedaStore store;
    private final ResponseMapper mapper;

    public AuthService(FankedaStore store, ResponseMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.code() == null || request.code().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "code不能为空");
        }
        FankedaStore.LoginSession session = store.login(request.code().trim());
        return new LoginResponse(session.token(), mapper.toProfileResponse(session.user()));
    }

    public UserProfile requireUser(String authorization) {
        String token = resolveToken(authorization);
        if (token == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        return store.findUserByToken(token)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录态无效"));
    }

    public UserProfile optionalUser(String authorization) {
        String token = resolveToken(authorization);
        if (token == null) {
            return null;
        }
        return store.findUserByToken(token).orElse(null);
    }

    private String resolveToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        String value = authorization.trim();
        if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return value.substring(7).trim();
        }
        return value;
    }
}
