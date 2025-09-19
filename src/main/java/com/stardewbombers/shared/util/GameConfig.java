package com.stardewbombers.shared.util;

/**
 * 游戏配置类
 * 包含游戏中的各种配置常量
 */
public class GameConfig {
    // 玩家配置
    public static final int PLAYER_MAX_HP = 2; // 玩家受到2次伤害后死亡
    public static final double PLAYER_BASE_SPEED = 2.0; // 提高玩家移动速度
    public static final long INVINCIBLE_MS_AFTER_HIT = 2000; // 2秒无敌时间
    
    // 炸弹配置
    public static final int INITIAL_BOMB_COUNT = 1;
    public static final int INITIAL_BOMB_POWER = 1;
    public static final double BOMB_FUSE_TIME = 3.0; // 增加引信时间到5秒
    public static final double BOMB_EXPLOSION_DURATION = 0.5; // 0.5秒爆炸持续时间
    
    // 地图配置
    public static final int MAP_WIDTH = 15;
    public static final int MAP_HEIGHT = 13;
    public static final int TILE_SIZE = 50;
    
    // 网络配置
    public static final int SERVER_PORT = 8080;
    public static final int MAX_PLAYERS = 4;
}