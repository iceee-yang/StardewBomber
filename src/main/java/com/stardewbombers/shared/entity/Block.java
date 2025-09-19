package com.stardewbombers.shared.entity;

import com.stardewbombers.shared.enums.BlockType;

/**
 * 地图块实体类
 * 表示游戏地图上的一个方块
 */
public class Block {
    private int x;
    private int y;
    private BlockType type;
    private boolean isDestroyed;
    private boolean hasBomb;
    private boolean hasPowerUp;
    
    public Block(int x, int y, BlockType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isDestroyed = false;
        this.hasBomb = false;
        this.hasPowerUp = false;
    }
    
    // Getters and Setters
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public BlockType getType() {
        return type;
    }
    
    public void setType(BlockType type) {
        this.type = type;
    }
    
    public boolean isDestroyed() {
        return isDestroyed;
    }
    
    public void setDestroyed(boolean destroyed) {
        this.isDestroyed = destroyed;
    }
    
    public boolean hasBomb() {
        return hasBomb;
    }
    
    public void setHasBomb(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }
    
    public boolean hasPowerUp() {
        return hasPowerUp;
    }
    
    public void setHasPowerUp(boolean hasPowerUp) {
        this.hasPowerUp = hasPowerUp;
    }
    
    /**
     * 检查方块是否可以被玩家穿过
     */
    public boolean isWalkable() {
        // 如果方块被破坏了，它变成了地板，应该是可通行的
        if (isDestroyed) {
            return true;
        }
        // 如果方块没有被破坏，检查其类型是否可通行
        return type.isWalkable();
    }
    
    /**
     * 检查方块是否可以被炸弹破坏
     */
    public boolean isDestructible() {
        return type.isExplorable() && !isDestroyed;
    }
    
    /**
     * 检查方块被破坏后是否有奖励
     */
    public boolean hasReward() {
        return type.isTreat();
    }
    
    /**
     * 破坏方块
     */
    public void destroy() {
        if (isDestructible()) {
            this.isDestroyed = true;
            this.type = BlockType.FLOOR; // 破坏后变成地板
        }
    }
    
    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, type=%s, destroyed=%s]", 
                           x, y, type.getName(), isDestroyed);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Block block = (Block) obj;
        return x == block.x && y == block.y;
    }
    
    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}
