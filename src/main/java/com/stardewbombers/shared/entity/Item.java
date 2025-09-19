package com.stardewbombers.shared.entity;

import com.stardewbombers.shared.enums.PowerUpType;
import javafx.geometry.Point2D;

/**
 * 道具实体类
 * 表示地图上掉落的道具
 */
public class Item {
    private PowerUpType type;
    private Point2D position;
    private boolean isCollected;
    private long spawnTime;
    private String texturePath;
    
    public Item(PowerUpType type, Point2D position) {
        this.type = type;
        this.position = position;
        this.isCollected = false;
        this.spawnTime = System.currentTimeMillis();
        this.texturePath = getTexturePath(type);
    }
    
    /**
     * 根据道具类型获取纹理路径
     */
    private String getTexturePath(PowerUpType type) {
        switch (type) {
            case BOOTS:
                return "/textures/boots.png";
            case LIFE_ELIXIR:
                return "/textures/life_elixir.png";
            case SPEED_BOOST:
                return "/textures/speed_boost.png";
            case SHIELD:
                return "/textures/shield.png";
            case BOMB_COUNT:
                return "/textures/bomb_count.png";
            case BOMB_POWER:
                return "/textures/bomb_power.png";
            default:
                return "/textures/unknown_item.png";
        }
    }
    
    /**
     * 拾取道具
     */
    public void collect() {
        this.isCollected = true;
    }
    
    /**
     * 检查道具是否已被拾取
     */
    public boolean isCollected() {
        return isCollected;
    }
    
    /**
     * 获取道具类型
     */
    public PowerUpType getType() {
        return type;
    }
    
    /**
     * 获取道具位置
     */
    public Point2D getPosition() {
        return position;
    }
    
    /**
     * 设置道具位置
     */
    public void setPosition(Point2D position) {
        this.position = position;
    }
    
    /**
     * 获取道具生成时间
     */
    public long getSpawnTime() {
        return spawnTime;
    }
    
    /**
     * 获取纹理路径
     */
    public String getTexturePath() {
        return texturePath;
    }
    
    /**
     * 获取道具在世界坐标中的网格位置
     */
    public Point2D getGridPosition() {
        int gridX = (int) (position.getX() / 40); // 假设网格大小为40
        int gridY = (int) (position.getY() / 40);
        return new Point2D(gridX, gridY);
    }
    
    @Override
    public String toString() {
        return String.format("Item[type=%s, position=(%.1f, %.1f), collected=%s]", 
                           type, position.getX(), position.getY(), isCollected);
    }
}
