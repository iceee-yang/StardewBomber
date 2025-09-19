package com.stardewbombers.shared.game;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.server.game.CollisionDetector;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 游戏管理器
 * 处理爆炸事件、伤害计算、碰撞检测等游戏逻辑
 */
public class GameManager {
    private final List<PlayerComponent> players = new ArrayList<>();
    private final List<ExplosionEvent> activeExplosions = new ArrayList<>();
    private final int gridSize = 40; // 与TILE_SIZE保持一致
    private GameMap gameMap;
    private CollisionDetector collisionDetector;
    private final GameEndDetector gameEndDetector;

    public GameManager() {
        this.gameEndDetector = new GameEndDetector();
    }

    /**
     * 设置游戏地图
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
        this.collisionDetector = new CollisionDetector(gameMap);
        
        // 为所有现有玩家设置碰撞检测器
        for (PlayerComponent player : players) {
            player.getMovement().setCollisionDetector(collisionDetector);
        }
    }
    
    /**
     * 设置游戏地图（带缩放）
     */
    public void setGameMap(GameMap gameMap, double scale) {
        this.gameMap = gameMap;
        this.collisionDetector = new CollisionDetector(gameMap, scale);
        
        // 为所有现有玩家设置碰撞检测器
        for (PlayerComponent player : players) {
            player.getMovement().setCollisionDetector(collisionDetector);
        }
    }

    /**
     * 获取游戏地图
     */
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * 获取碰撞检测器
     */
    public CollisionDetector getCollisionDetector() {
        return collisionDetector;
    }

    /**
     * 添加玩家
     */
    public void addPlayer(PlayerComponent playerComponent) {
        players.add(playerComponent);
        gameEndDetector.addPlayer(playerComponent);
        
        // 为新玩家设置碰撞检测器
        if (collisionDetector != null) {
            playerComponent.getMovement().setCollisionDetector(collisionDetector);
        }
    }

    /**
     * 移除玩家
     */
    public void removePlayer(PlayerComponent playerComponent) {
        players.remove(playerComponent);
        gameEndDetector.removePlayer(playerComponent);
    }

    /**
     * 处理爆炸事件
     */
    public void handleExplosion(Bomb bomb) {
        System.out.println("=== GameManager处理爆炸事件 ===");
        System.out.println("炸弹: " + bomb.getOwnerId() + " 位置: (" + bomb.getX() + ", " + bomb.getY() + ") 时间: " + System.currentTimeMillis());
        
        // 创建爆炸事件
        List<Point2D> affectedPositions = calculateExplosionRange(bomb);
        ExplosionEvent explosionEvent = new ExplosionEvent(bomb, affectedPositions, System.currentTimeMillis());
        activeExplosions.add(explosionEvent);

        // 处理对玩家的伤害
        for (PlayerComponent playerComponent : players) {
            playerComponent.handleExplosionDamage(explosionEvent);
        }

        // 处理对地图方块的破坏
        if (gameMap != null) {
            handleMapDamage(explosionEvent);
        }
        
        System.out.println("=== GameManager爆炸事件处理完成 ===");
    }

    /**
     * 处理爆炸对地图方块的破坏
     */
    private void handleMapDamage(ExplosionEvent explosionEvent) {
        System.out.println("=== 开始处理地图方块破坏 ===");
        for (Point2D pos : explosionEvent.getAffectedPositions()) {
            // 将世界坐标转换为网格坐标
            int gridX = (int) (pos.getX() / gridSize);
            int gridY = (int) (pos.getY() / gridSize);
            
            // 检查方块状态
            Block block = gameMap.getBlock(gridX, gridY);
            if (block != null) {
                System.out.printf("检查位置(%d,%d): %s, 可破坏=%s, 已破坏=%s%n", 
                    gridX, gridY, block.getType().getName(), 
                    block.isDestructible(), block.isDestroyed());
                
                // 破坏方块
                boolean destroyed = gameMap.destroyBlock(gridX, gridY);
                System.out.printf("破坏结果: %s%n", destroyed ? "成功" : "失败");
            } else {
                System.out.printf("位置(%d,%d)没有方块%n", gridX, gridY);
            }
        }
        System.out.println("=== 地图方块破坏处理完成 ===");
    }

    /**
     * 计算爆炸范围（固定十字形，以炸弹所在格子为中心，总共5个格子）
     */
    private List<Point2D> calculateExplosionRange(Bomb bomb) {
        List<Point2D> affectedPositions = new ArrayList<>();
        
        // 将炸弹坐标对齐到格子中心
        int col = (int)(bomb.getWorldX() / gridSize);
        int row = (int)(bomb.getWorldY() / gridSize);
        
        // 直接使用格子坐标计算左上角位置
        double centerX = col * gridSize;
        double centerY = row * gridSize;

        // 中心点（炸弹所在格子）
        affectedPositions.add(new Point2D(centerX, centerY));

        // 上下左右各1格（固定十字形，总共5个格子），但需要检查边界
        // 上
        if (row > 0) {
            affectedPositions.add(new Point2D(centerX, centerY - gridSize));
        }
        // 下
        if (row < gameMap.getHeight() - 1) {
            affectedPositions.add(new Point2D(centerX, centerY + gridSize));
        }
        // 左
        if (col > 0) {
            affectedPositions.add(new Point2D(centerX - gridSize, centerY));
        }
        // 右
        if (col < gameMap.getWidth() - 1) {
            affectedPositions.add(new Point2D(centerX + gridSize, centerY));
        }

        return affectedPositions;
    }

    /**
     * 检查玩家碰撞
     */
    public boolean checkPlayerCollision(PlayerComponent player1, PlayerComponent player2) {
        Point2D pos1 = player1.getPlayer().getPosition();
        Point2D pos2 = player2.getPlayer().getPosition();
        
        // 简单的圆形碰撞检测
        double distance = pos1.distance(pos2);
        return distance < 30; // 玩家半径约15像素
    }

    /**
     * 检查玩家与炸弹碰撞
     */
    public boolean checkBombCollision(PlayerComponent playerComponent, Bomb bomb) {
        Point2D playerPos = playerComponent.getPlayer().getPosition();
        Point2D bombPos = new Point2D(bomb.getX() * gridSize, bomb.getY() * gridSize);
        
        double distance = playerPos.distance(bombPos);
        return distance < 25; // 炸弹半径约12.5像素
    }

    /**
     * 更新游戏状态
     */
    public void update(long nowMs) {
        // 更新所有玩家
        for (PlayerComponent playerComponent : players) {
            playerComponent.tick(nowMs);
            
            // 检查炸弹爆炸
            List<Bomb> explodedBombs = playerComponent.getBombs().tick(nowMs);
            for (Bomb bomb : explodedBombs) {
                handleExplosion(bomb);
            }
        }

        // 检查游戏结束条件
        gameEndDetector.checkGameEnd();

        // 清理过期的爆炸效果
        activeExplosions.removeIf(explosion -> 
            nowMs - explosion.getExplosionTime() > 2000); // 爆炸效果持续2秒
    }

    /**
     * 获取所有玩家
     */
    public List<PlayerComponent> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * 获取活跃爆炸
     */
    public List<ExplosionEvent> getActiveExplosions() {
        return new ArrayList<>(activeExplosions);
    }
    
    /**
     * 添加游戏结束回调
     */
    public void addGameEndCallback(Consumer<String> callback) {
        gameEndDetector.addGameEndCallback(callback);
    }
    
    /**
     * 获取游戏结束检测器
     */
    public GameEndDetector getGameEndDetector() {
        return gameEndDetector;
    }
    
    /**
     * 重置游戏状态
     */
    public void resetGame() {
        gameEndDetector.reset();
    }
}
