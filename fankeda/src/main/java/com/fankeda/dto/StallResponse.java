package com.fankeda.dto;

public record StallResponse(
    Long stallId,
    String name,
    String type,
    int posX,
    int posY,
    double serveSpeed,
    int distance,
    double avgPrep,
    double rating,
    int queueCount,
    double waitTime,
    double walkTime,
    double totalTime,
    String level
) {
}
