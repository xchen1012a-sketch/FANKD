package com.fankeda.controller;

import com.fankeda.api.ApiResponse;
import com.fankeda.dto.QueueReportRequest;
import com.fankeda.dto.QueueSnapshotResponse;
import com.fankeda.service.QueueService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/report")
    ApiResponse<QueueSnapshotResponse> report(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody QueueReportRequest request
    ) {
        return ApiResponse.ok(queueService.report(authorization, request));
    }

    @GetMapping("/latest")
    ApiResponse<List<QueueSnapshotResponse>> latest() {
        return ApiResponse.ok(queueService.latest());
    }
}
