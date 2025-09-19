package com.stardewbombers.shared.game;

import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.shared.entity.Player;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 游戏结束检测器
 * 负责检测游戏结束条件并触发相应的回调
 */
public class GameEndDetector {
    private final List<PlayerComponent> players;
    private final List<Consumer<String>> gameEndCallbacks;
    private boolean gameEnded = false;
    
    public GameEndDetector() {
        this.players = new ArrayList<>();
        this.gameEndCallbacks = new ArrayList<>();
    }
    
    /**
     * 添加玩家
     */
    public void addPlayer(PlayerComponent playerComponent) {
        if (!players.contains(playerComponent)) {
            players.add(playerComponent);
        }
    }
    
    /**
     * 移除玩家
     */
    public void removePlayer(PlayerComponent playerComponent) {
        players.remove(playerComponent);
    }
    
    /**
     * 添加游戏结束回调
     * @param callback 当游戏结束时调用的回调函数，参数为获胜玩家ID
     */
    public void addGameEndCallback(Consumer<String> callback) {
        gameEndCallbacks.add(callback);
    }
    
    /**
     * 检查游戏是否应该结束
     * 当只有一个或没有玩家存活时，游戏结束
     */
    public void checkGameEnd() {
        if (gameEnded) {
            return; // 游戏已经结束，不再检查
        }
        
        List<PlayerComponent> alivePlayers = new ArrayList<>();
        String lastAlivePlayerId = null;
        
        // 统计存活玩家
        for (PlayerComponent playerComponent : players) {
            if (playerComponent.getPlayer().isAlive()) {
                alivePlayers.add(playerComponent);
                lastAlivePlayerId = playerComponent.getPlayer().getId();
            }
        }
        
        // 检查游戏结束条件
        if (alivePlayers.size() <= 1) {
            gameEnded = true;
            
            // 确定获胜者
            String winnerId = null;
            if (alivePlayers.size() == 1) {
                winnerId = lastAlivePlayerId;
            }
            // 如果所有玩家都死亡，winnerId为null（平局）
            
            // 触发游戏结束回调
            for (Consumer<String> callback : gameEndCallbacks) {
                try {
                    callback.accept(winnerId);
                } catch (Exception e) {
                    System.err.println("游戏结束回调执行失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("游戏结束！获胜者: " + (winnerId != null ? winnerId : "平局"));
        }
    }
    
    /**
     * 重置游戏状态
     */
    public void reset() {
        gameEnded = false;
    }
    
    /**
     * 获取存活玩家数量
     */
    public int getAlivePlayerCount() {
        int count = 0;
        for (PlayerComponent playerComponent : players) {
            if (playerComponent.getPlayer().isAlive()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 获取所有玩家
     */
    public List<PlayerComponent> getPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * 检查游戏是否已结束
     */
    public boolean isGameEnded() {
        return gameEnded;
    }
    
    /**
     * 获取存活玩家列表
     */
    public List<PlayerComponent> getAlivePlayers() {
        List<PlayerComponent> alivePlayers = new ArrayList<>();
        for (PlayerComponent playerComponent : players) {
            if (playerComponent.getPlayer().isAlive()) {
                alivePlayers.add(playerComponent);
            }
        }
        return alivePlayers;
    }
    
    /**
     * 获取死亡玩家列表
     */
    public List<PlayerComponent> getDeadPlayers() {
        List<PlayerComponent> deadPlayers = new ArrayList<>();
        for (PlayerComponent playerComponent : players) {
            if (!playerComponent.getPlayer().isAlive()) {
                deadPlayers.add(playerComponent);
            }
        }
        return deadPlayers;
    }
}
