package com.stardewbombers.shared.protocol;

import java.util.UUID;

public abstract class Message {
    private String messageId = UUID.randomUUID().toString();
    private MessageType type;
    private String senderId;
    private long timestamp = System.currentTimeMillis();

    public Message(MessageType type, String senderId) {
        this.type = type;
        this.senderId = senderId;
    }

    // Getter和Setter方法
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public long getTimestamp() { return timestamp; }
    public String getMessageId() { return messageId; }
}
