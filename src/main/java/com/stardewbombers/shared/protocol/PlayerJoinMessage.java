package com.stardewbombers.shared.protocol;

public class PlayerJoinMessage extends Message {
    private String playerName;

    public PlayerJoinMessage() {
        super(MessageType.PLAYER_JOIN, "");
    }

    public PlayerJoinMessage(String playerId, String playerName) {
        super(MessageType.PLAYER_JOIN, playerId);
        this.playerName = playerName;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
}
