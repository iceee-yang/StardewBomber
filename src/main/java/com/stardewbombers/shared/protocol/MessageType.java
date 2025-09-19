package com.stardewbombers.shared.protocol;

public enum MessageType {
    // 连接相关
    PLAYER_JOIN,
    PLAYER_LEAVE,
    HEARTBEAT,

    // 游戏操作
    PLAYER_MOVE,
    PLACE_BOMB,
    BOMB_EXPLODE,
    COLLECT_POWERUP,

    // 农场操作
    PLANT_SEED,
    HARVEST_CROP,
    WATER_CROP,

    // 游戏状态
    GAME_START,
    GAME_END,
    PLAYER_DEATH,

    // 服务器响应
    SERVER_RESPONSE,
    ERROR_MESSAGE
}
