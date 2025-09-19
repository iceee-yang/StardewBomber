package com.stardewbombers.client;

import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.Item;
import com.stardewbombers.shared.entity.ItemManager;
import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.enums.PowerUpType;
import javafx.geometry.Point2D;

/**
 * 道具系统测试类
 * 用于测试道具掉落、拾取和效果
 */
public class ItemSystemTest {
    
    public static void main(String[] args) {
        System.out.println("=== 道具系统测试开始 ===");
        
        // 测试1: 道具管理器基本功能
        testItemManager();
        
        // 测试2: 道具掉落概率
        testItemDropProbability();
        
        // 测试3: 道具拾取
        testItemPickup();
        
        // 测试4: 玩家道具效果
        testPlayerItemEffects();
        
        System.out.println("=== 道具系统测试结束 ===");
    }
    
    /**
     * 测试道具管理器基本功能
     */
    private static void testItemManager() {
        System.out.println("\n--- 测试1: 道具管理器基本功能 ---");
        
        ItemManager itemManager = new ItemManager();
        
        // 测试生成道具
        Point2D position1 = new Point2D(100, 100);
        Point2D position2 = new Point2D(200, 200);
        
        boolean spawned1 = itemManager.trySpawnItem(position1, true);
        boolean spawned2 = itemManager.trySpawnItem(position2, false);
        
        System.out.println("有奖励方块生成道具: " + spawned1);
        System.out.println("无奖励方块生成道具: " + spawned2);
        System.out.println("当前道具数量: " + itemManager.getItemCount());
        
        // 测试拾取道具
        Point2D playerPos = new Point2D(105, 105); // 接近第一个道具
        var pickedUp = itemManager.checkItemPickup(playerPos, 20);
        System.out.println("拾取道具数量: " + pickedUp.size());
        System.out.println("拾取后道具数量: " + itemManager.getItemCount());
    }
    
    /**
     * 测试道具掉落概率
     */
    private static void testItemDropProbability() {
        System.out.println("\n--- 测试2: 道具掉落概率 ---");
        
        ItemManager itemManager = new ItemManager();
        int totalAttempts = 1000;
        int bootsCount = 0;
        int lifeElixirCount = 0;
        int noDropCount = 0;
        
        for (int i = 0; i < totalAttempts; i++) {
            Point2D position = new Point2D(i * 10, 0);
            boolean spawned = itemManager.trySpawnItem(position, true);
            
            if (spawned) {
                var items = itemManager.getActiveItems();
                if (!items.isEmpty()) {
                    PowerUpType type = items.get(items.size() - 1).getType();
                    if (type == PowerUpType.BOOTS) {
                        bootsCount++;
                    } else if (type == PowerUpType.LIFE_ELIXIR) {
                        lifeElixirCount++;
                    }
                }
            } else {
                noDropCount++;
            }
        }
        
        System.out.println("总尝试次数: " + totalAttempts);
        System.out.println("Boots掉落次数: " + bootsCount + " (" + (bootsCount * 100.0 / totalAttempts) + "%)");
        System.out.println("Life_Elixir掉落次数: " + lifeElixirCount + " (" + (lifeElixirCount * 100.0 / totalAttempts) + "%)");
        System.out.println("无掉落次数: " + noDropCount + " (" + (noDropCount * 100.0 / totalAttempts) + "%)");
        
        if (bootsCount > 0 && lifeElixirCount > 0) {
            double ratio = (double) bootsCount / lifeElixirCount;
            System.out.println("Boots:Life_Elixir 比例: " + String.format("%.2f", ratio) + ":1");
        }
    }
    
    /**
     * 测试道具拾取
     */
    private static void testItemPickup() {
        System.out.println("\n--- 测试3: 道具拾取 ---");
        
        ItemManager itemManager = new ItemManager();
        
        // 生成一些道具
        Point2D[] positions = {
            new Point2D(100, 100),
            new Point2D(200, 200),
            new Point2D(300, 300)
        };
        
        for (Point2D pos : positions) {
            itemManager.trySpawnItem(pos, true);
        }
        
        System.out.println("生成道具数量: " + itemManager.getItemCount());
        
        // 模拟玩家移动拾取道具
        Point2D[] playerPositions = {
            new Point2D(105, 105), // 接近第一个道具
            new Point2D(250, 250), // 接近第二个道具
            new Point2D(400, 400)  // 远离所有道具
        };
        
        for (int i = 0; i < playerPositions.length; i++) {
            var pickedUp = itemManager.checkItemPickup(playerPositions[i], 20);
            System.out.println("玩家位置 " + (i+1) + " 拾取道具数量: " + pickedUp.size());
        }
        
        System.out.println("最终剩余道具数量: " + itemManager.getItemCount());
    }
    
    /**
     * 测试玩家道具效果
     */
    private static void testPlayerItemEffects() {
        System.out.println("\n--- 测试4: 玩家道具效果 ---");
        
        Player player = new Player("test_player", new Point2D(100, 100));
        
        System.out.println("初始生命值: " + player.getHealth());
        System.out.println("初始速度: " + player.getSpeed());
        
        // 测试boots效果
        System.out.println("\n应用boots效果:");
        player.addPowerUp(PowerUpType.BOOTS);
        System.out.println("应用后速度: " + player.getSpeed());
        
        // 模拟时间流逝
        System.out.println("\n模拟5秒后:");
        player.tick(System.currentTimeMillis() + 5000);
        System.out.println("5秒后速度: " + player.getSpeed());
        
        System.out.println("\n模拟11秒后:");
        player.tick(System.currentTimeMillis() + 11000);
        System.out.println("11秒后速度: " + player.getSpeed());
        
        // 测试生命药水效果
        System.out.println("\n测试生命药水效果:");
        player.takeDamage(2, System.currentTimeMillis()); // 受到2点伤害
        System.out.println("受伤后生命值: " + player.getHealth());
        
        player.addPowerUp(PowerUpType.LIFE_ELIXIR);
        System.out.println("使用生命药水后生命值: " + player.getHealth());
        
        player.addPowerUp(PowerUpType.LIFE_ELIXIR);
        System.out.println("再次使用生命药水后生命值: " + player.getHealth());
    }
}
