package com.stardewbombers.shared.entity;

import javafx.geometry.Point2D;
import java.util.List;

/**
 * 爆炸事件类
 * 用于处理爆炸伤害和影响范围
 */
public class ExplosionEvent {
    private final Bomb bomb;
    private final List<Point2D> affectedPositions;
    private final long explosionTime;

    public ExplosionEvent(Bomb bomb, List<Point2D> affectedPositions, long explosionTime) {
        this.bomb = bomb;
        this.affectedPositions = affectedPositions;
        this.explosionTime = explosionTime;
    }

    public Bomb getBomb() { return bomb; }
    public List<Point2D> getAffectedPositions() { return affectedPositions; }
    public long getExplosionTime() { return explosionTime; }
    public String getOwnerId() { return bomb.getOwnerId(); }
}

