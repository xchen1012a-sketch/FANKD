package com.fankeda.dto;

import java.util.List;

public record RecommendResponse(RecommendStallResponse best, List<RecommendStallResponse> stalls) {
}
