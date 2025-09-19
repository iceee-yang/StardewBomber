package com.stardewbombers.shared.protocol;

import javafx.geometry.Point2D;

public class PlaceBombMessage extends Message {
    private Point2D position;
    private int explosionRange;

    public PlaceBombMessage() {
        super(MessageType.PLACE_BOMB, "");
    }

    public PlaceBombMessage(String playerId, Point2D position, int explosionRange) {
        super(MessageType.PLACE_BOMB, playerId);
        this.position = position;
        this.explosionRange = explosionRange;
    }

    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public int getExplosionRange() { return explosionRange; }
    public void setExplosionRange(int explosionRange) { this.explosionRange = explosionRange; }
}
