package com.fankeda.dto;

public record RecommendStallResponse(
    Long stallId,
    String name,
    double score,
    String level,
    double totalTime,
    double waitTime,
    double walkTime,
    double prepTime,
    int queueCount,
    int distance,
    double rating
) {
}
