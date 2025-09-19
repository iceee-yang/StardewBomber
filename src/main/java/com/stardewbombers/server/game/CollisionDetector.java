package com.stardewbombers.server.game;

import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.component.PlayerComponent;
import javafx.geometry.Point2D;

/**
 * 碰撞检测器
 * 负责处理游戏中的各种碰撞检测
 */
public class CollisionDetector {
    private GameMap gameMap;
    private double scale;
    
    public CollisionDetector(GameMap gameMap) {
        this.gameMap = gameMap;
        this.scale = 1.0; // 默认缩放
    }
    
    public CollisionDetector(GameMap gameMap, double scale) {
        this.gameMap = gameMap;
        this.scale = scale;
    }
    
    /**
     * 检查指定位置是否可通行
     * @param x 世界坐标X
     * @param y 世界坐标Y
     * @return 是否可通行
     */
    public boolean isPositionWalkable(double x, double y) {
        // 首先检查是否在地图边界内
        if (!isWithinMapBounds(x, y)) {
            return false;
        }
        
        // 将世界坐标转换为网格坐标，考虑缩放
        int gridX = (int) (x / (gameMap.getTileWidth() * scale));
        int gridY = (int) (y / (gameMap.getTileHeight() * scale));
        
        return gameMap.isWalkable(gridX, gridY);
    }
    
    /**
     * 检查指定网格位置是否可通行
     * @param gridX 网格X坐标
     * @param gridY 网格Y坐标
     * @return 是否可通行
     */
    public boolean isGridPositionWalkable(int gridX, int gridY) {
        // 首先检查是否在地图边界内
        if (gridX < 0 || gridX >= gameMap.getWidth() || gridY < 0 || gridY >= gameMap.getHeight()) {
            return false;
        }
        
        return gameMap.isWalkable(gridX, gridY);
    }
    
    /**
     * 检查世界坐标是否在地图边界内
     * @param x 世界坐标X
     * @param y 世界坐标Y
     * @return 是否在边界内
     */
    public boolean isWithinMapBounds(double x, double y) {
        double mapWidth = gameMap.getWidth() * gameMap.getTileWidth() * scale;
        double mapHeight = gameMap.getHeight() * gameMap.getTileHeight() * scale;
        
        return x >= 0 && x < mapWidth && y >= 0 && y < mapHeight;
    }
    
    /**
     * 检查玩家是否可以移动到目标位置
     * @param currentPos 当前位置
     * @param targetPos 目标位置
     * @return 是否可以移动
     */
    public boolean canMoveTo(Point2D currentPos, Point2D targetPos) {
        // 检查目标位置是否可通行
        if (!isPositionWalkable(targetPos.getX(), targetPos.getY())) {
            return false;
        }
        
        // 检查移动路径上是否有障碍物（可选，用于更精确的碰撞检测）
        return true;
    }
    
    /**
     * 检查玩家与地图边界的碰撞
     * @param pos 玩家位置
     * @param playerRadius 玩家半径
     * @return 是否碰撞边界
     */
    public boolean checkBoundaryCollision(Point2D pos, double playerRadius) {
        double mapWidth = gameMap.getWidth() * gameMap.getTileWidth() * scale;
        double mapHeight = gameMap.getHeight() * gameMap.getTileHeight() * scale;
        
        return pos.getX() - playerRadius < 0 || 
               pos.getX() + playerRadius > mapWidth ||
               pos.getY() - playerRadius < 0 || 
               pos.getY() + playerRadius > mapHeight;
    }
    
    /**
     * 获取玩家在网格中的位置
     * @param worldPos 世界坐标
     * @return 网格坐标
     */
    public Point2D getGridPosition(Point2D worldPos) {
        int gridX = (int) (worldPos.getX() / (gameMap.getTileWidth() * scale));
        int gridY = (int) (worldPos.getY() / (gameMap.getTileHeight() * scale));
        return new Point2D(gridX, gridY);
    }
    
    /**
     * 获取网格位置的世界坐标（网格中心点）
     * @param gridX 网格X坐标
     * @param gridY 网格Y坐标
     * @return 世界坐标
     */
    public Point2D getWorldPosition(int gridX, int gridY) {
        double worldX = gridX * gameMap.getTileWidth() * scale + (gameMap.getTileWidth() * scale) / 2.0;
        double worldY = gridY * gameMap.getTileHeight() * scale + (gameMap.getTileHeight() * scale) / 2.0;
        return new Point2D(worldX, worldY);
    }
    
    /**
     * 检查两个玩家是否碰撞
     * @param player1 玩家1
     * @param player2 玩家2
     * @param collisionRadius 碰撞半径
     * @return 是否碰撞
     */
    public boolean checkPlayerCollision(PlayerComponent player1, PlayerComponent player2, double collisionRadius) {
        Point2D pos1 = player1.getPlayer().getPosition();
        Point2D pos2 = player2.getPlayer().getPosition();
        
        double distance = pos1.distance(pos2);
        return distance < collisionRadius * 2; // 两个玩家的半径之和
    }
    
    /**
     * 设置地图引用
     * @param gameMap 游戏地图
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
    
    /**
     * 获取当前地图
     * @return 游戏地图
     */
    public GameMap getGameMap() {
        return gameMap;
    }
}
