package com.stardewbombers.farm;

/**
 * 农场服务接口
 */
public interface FarmService {
    /**
     * 获取玩家的农场
     */
    Farm getFarm(String playerId);
    
    /**
     * 种植种子
     */
    boolean plantSeed(String playerId, int x, int y, Farm.CropType cropType);
    
    /**
     * 浇水
     */
    boolean waterCrop(String playerId, int x, int y);
    
    /**
     * 收获作物
     */
    Farm.CropType harvestCrop(String playerId, int x, int y);
    
    /**
     * 清理枯死的作物
     */
    boolean clearDeadCrop(String playerId, int x, int y);
    
    /**
     * 更新农场状态
     */
    void updateFarm(String playerId);
    
    /**
     * 获取种子数量（带playerId）
     */
    int getSeedCount(String playerId, Farm.CropType cropType);
    
    /**
     * 获取作物数量（带playerId）
     */
    int getCropCount(String playerId, Farm.CropType cropType);
    
    /**
     * 添加种子（带playerId）
     */
    void addSeed(String playerId, Farm.CropType cropType, int amount);
    
    /**
     * 添加作物（带playerId）
     */
    void addCrop(String playerId, Farm.CropType cropType, int amount);
    
    /**
     * 获取金币数量（带playerId）
     */
    int getGoldCount(String playerId);
    
    /**
     * 添加金币（带playerId）
     */
    void addGold(String playerId, int amount);
    
    // ========== 单机版简化方法（不需要playerId） ==========
    
    /**
     * 获取种子数量（单机版）
     */
    default int getSeedCount(Farm.CropType cropType) {
        return getSeedCount("singlePlayer", cropType);
    }
    
    /**
     * 获取作物数量（单机版）
     */
    default int getCropCount(Farm.CropType cropType) {
        return getCropCount("singlePlayer", cropType);
    }
    
    /**
     * 添加种子（单机版）
     */
    default void addSeed(Farm.CropType cropType, int amount) {
        addSeed("singlePlayer", cropType, amount);
    }
    
    /**
     * 添加作物（单机版）
     */
    default void addCrop(Farm.CropType cropType, int amount) {
        addCrop("singlePlayer", cropType, amount);
    }
    
    /**
     * 获取金币数量（单机版）
     */
    default int getGoldCount() {
        return getGoldCount("singlePlayer");
    }
    
    /**
     * 添加金币（单机版）
     */
    default void addGold(int amount) {
        addGold("singlePlayer", amount);
    }
}
