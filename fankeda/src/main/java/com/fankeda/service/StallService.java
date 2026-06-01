package com.fankeda.service;

import com.fankeda.dto.StallResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StallService {

    private final FankedaStore store;
    private final ResponseMapper mapper;

    public StallService(FankedaStore store, ResponseMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }

    public List<StallResponse> listStalls() {
        return store.stalls().stream()
            .map(stall -> mapper.toStallResponse(stall, store.latestSnapshot(stall.id())))
            .toList();
    }
}
