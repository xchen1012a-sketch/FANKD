package com.fankeda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stalls")
public class StallEntity {

    @Id
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 80)
    private String type;

    @Column(name = "pos_x", nullable = false)
    private int posX;

    @Column(name = "pos_y", nullable = false)
    private int posY;

    @Column(name = "serve_speed", nullable = false)
    private double serveSpeed;

    @Column(nullable = false)
    private int distance;

    @Column(name = "avg_prep", nullable = false)
    private double avgPrep;

    @Column(nullable = false)
    private double rating;

    protected StallEntity() {
    }

    public StallEntity(
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
        this.id = id;
        this.name = name;
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.serveSpeed = serveSpeed;
        this.distance = distance;
        this.avgPrep = avgPrep;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public double getServeSpeed() {
        return serveSpeed;
    }

    public int getDistance() {
        return distance;
    }

    public double getAvgPrep() {
        return avgPrep;
    }

    public double getRating() {
        return rating;
    }
}
