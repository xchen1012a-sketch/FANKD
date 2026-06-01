package com.fankeda.model;

public record Stall(
    Long id,
    String name,
    String type,
    int posX,
    int posY,
    double serveSpeed,
    int distance,
    double avgPrep,
    double rating
) {

    public Stall withServeSpeed(double adjustedServeSpeed) {
        return new Stall(id, name, type, posX, posY, adjustedServeSpeed, distance, avgPrep, rating);
    }
}
