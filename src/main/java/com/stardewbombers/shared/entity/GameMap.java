package com.stardewbombers.shared.entity;

import com.stardewbombers.shared.enums.BlockType;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 * 游戏地图类
 * 包含整个地图的数据和操作
 */
public class GameMap {
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private Block[][] blocks;
    private List<Block> destructibleBlocks;
    private ItemManager itemManager;
    
    public GameMap(int width, int height, int tileWidth, int tileHeight) {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.blocks = new Block[width][height];
        this.destructibleBlocks = new ArrayList<>();
        this.itemManager = new ItemManager();
        
        // 初始化所有方块为地板
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                blocks[x][y] = new Block(x, y, BlockType.FLOOR);
            }
        }
    }
    
    /**
     * 设置指定位置的方块类型
     */
    public void setBlock(int x, int y, BlockType type) {
        if (isValidPosition(x, y)) {
            Block block = new Block(x, y, type);
            blocks[x][y] = block;
            
            // 如果是可破坏的方块，添加到列表中
            if (type.isExplorable()) {
                destructibleBlocks.add(block);
            }
        }
    }
    
    /**
     * 获取指定位置的方块
     */
    public Block getBlock(int x, int y) {
        if (isValidPosition(x, y)) {
            return blocks[x][y];
        }
        return null;
    }
    
    /**
     * 检查位置是否有效
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * 检查位置是否可通行
     */
    public boolean isWalkable(int x, int y) {
        Block block = getBlock(x, y);
        return block != null && block.isWalkable();
    }
    
    /**
     * 检查位置是否可破坏
     */
    public boolean isDestructible(int x, int y) {
        Block block = getBlock(x, y);
        return block != null && block.isDestructible();
    }
    
    /**
     * 破坏指定位置的方块
     */
    public boolean destroyBlock(int x, int y) {
        Block block = getBlock(x, y);
        if (block != null && block.isDestructible()) {
            // 检查方块是否有奖励，如果有则尝试生成道具
            if (block.hasReward()) {
                // 计算方块的世界坐标中心位置
                double worldX = x * tileWidth + tileWidth / 2.0;
                double worldY = y * tileHeight + tileHeight / 2.0;
                Point2D itemPosition = new Point2D(worldX, worldY);
                
                // 尝试生成道具
                itemManager.trySpawnItem(itemPosition, true);
            }
            
            block.destroy();
            destructibleBlocks.remove(block);
            return true;
        }
        return false;
    }
    
    /**
     * 获取所有可破坏的方块
     */
    public List<Block> getDestructibleBlocks() {
        return new ArrayList<>(destructibleBlocks);
    }
    
    /**
     * 获取地图宽度
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * 获取地图高度
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * 获取方块宽度
     */
    public int getTileWidth() {
        return tileWidth;
    }
    
    /**
     * 获取方块高度
     */
    public int getTileHeight() {
        return tileHeight;
    }
    
    /**
     * 获取所有方块
     */
    public Block[][] getBlocks() {
        return blocks;
    }
    
    /**
     * 获取道具管理器
     */
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    /**
     * 获取地图的字符串表示（用于调试）
     */
    public String getMapString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Block block = blocks[x][y];
                if (block.isDestroyed()) {
                    sb.append(".");
                } else {
                    switch (block.getType()) {
                        case FLOOR:
                            sb.append(" ");
                            break;
                        case PUMPKIN:
                            sb.append("P");
                            break;
                        case MELON:
                            sb.append("M");
                            break;
                        case BUSHES:
                            sb.append("B");
                            break;
                        case STUMP:
                            sb.append("S");
                            break;
                        default:
                            sb.append("?");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
