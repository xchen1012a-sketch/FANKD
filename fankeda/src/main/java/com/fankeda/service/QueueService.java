package com.fankeda.service;

import com.fankeda.api.ApiException;
import com.fankeda.dto.QueueReportRequest;
import com.fankeda.dto.QueueSnapshotResponse;
import com.fankeda.model.QueueSnapshot;
import com.fankeda.model.UserProfile;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final FankedaStore store;
    private final AuthService authService;
    private final ResponseMapper mapper;

    public QueueService(FankedaStore store, AuthService authService, ResponseMapper mapper) {
        this.store = store;
        this.authService = authService;
        this.mapper = mapper;
    }

    public QueueSnapshotResponse report(String authorization, QueueReportRequest request) {
        validate(request);
        UserProfile user = authService.optionalUser(authorization);
        Long reporterId = user == null ? null : user.id();
        QueueSnapshot snapshot = store.addQueueSnapshot(request.stallId(), request.queueCount(), reporterId);
        return mapper.toQueueSnapshotResponse(snapshot);
    }

    public List<QueueSnapshotResponse> latest() {
        return store.stalls().stream()
            .map(stall -> store.latestSnapshot(stall.id()))
            .map(mapper::toQueueSnapshotResponse)
            .toList();
    }

    private void validate(QueueReportRequest request) {
        if (request == null || request.stallId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "stallId不能为空");
        }
        if (request.queueCount() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "queueCount不能为空");
        }
        if (request.queueCount() < 0 || request.queueCount() > 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "queueCount必须在0到200之间");
        }
    }
}
