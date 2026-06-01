package com.fankeda.dto;

public record AdminStallRequest(
    String name,
    String type,
    Integer posX,
    Integer posY,
    Double serveSpeed,
    Integer distance,
    Double avgPrep,
    Double rating
) {
}
