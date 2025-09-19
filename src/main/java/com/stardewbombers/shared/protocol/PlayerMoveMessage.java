package com.stardewbombers.shared.protocol;

import javafx.geometry.Point2D;

public class PlayerMoveMessage extends Message {
    private Point2D position;
    private String direction;

    // 默认构造函数（JSON反序列化需要）
    public PlayerMoveMessage() {
        super(MessageType.PLAYER_MOVE, "");
    }

    public PlayerMoveMessage(String playerId, Point2D position, String direction) {
        super(MessageType.PLAYER_MOVE, playerId);
        this.position = position;
        this.direction = direction;
    }

    // Getter和Setter
    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
