package com.stardewbombers.shared.entity;

import com.stardewbombers.shared.enums.PowerUpType;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 道具管理器
 * 负责管理地图上的道具生成、更新和拾取
 */
public class ItemManager {
    private List<Item> items;
    private Random random;
    private static final double DROP_PROBABILITY = 0.5; // 50%掉落概率
    private static final double BOOTS_WEIGHT = 4.0; // boots权重
    private static final double LIFE_ELIXIR_WEIGHT = 1.0; // life_elixir权重
    private static final double TOTAL_WEIGHT = BOOTS_WEIGHT + LIFE_ELIXIR_WEIGHT;
    
    public ItemManager() {
        this.items = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * 尝试在指定位置生成道具
     * @param position 生成位置
     * @param hasReward 方块是否有奖励
     * @return 是否成功生成道具
     */
    public boolean trySpawnItem(Point2D position, boolean hasReward) {
        if (!hasReward) {
            return false;
        }
        
        // 50%概率掉落道具
        if (random.nextDouble() < DROP_PROBABILITY) {
            PowerUpType itemType = generateRandomItemType();
            Item item = new Item(itemType, position);
            items.add(item);
            System.out.println("生成道具: " + itemType + " 位置: (" + position.getX() + ", " + position.getY() + ")");
            return true;
        }
        
        return false;
    }
    
    /**
     * 根据权重随机生成道具类型
     * boots:Life_Elixir = 4:1
     */
    private PowerUpType generateRandomItemType() {
        double randomValue = random.nextDouble() * TOTAL_WEIGHT;
        
        if (randomValue < BOOTS_WEIGHT) {
            return PowerUpType.BOOTS;
        } else {
            return PowerUpType.LIFE_ELIXIR;
        }
    }
    
    /**
     * 检查玩家是否拾取了道具
     * @param playerPosition 玩家位置
     * @param pickupRadius 拾取半径
     * @return 拾取的道具列表
     */
    public List<Item> checkItemPickup(Point2D playerPosition, double pickupRadius) {
        List<Item> pickedUpItems = new ArrayList<>();
        Iterator<Item> iterator = items.iterator();
        
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (!item.isCollected()) {
                double distance = playerPosition.distance(item.getPosition());
                if (distance <= pickupRadius) {
                    item.collect();
                    pickedUpItems.add(item);
                    iterator.remove();
                    System.out.println("玩家拾取道具: " + item.getType() + " 位置: (" + 
                                     item.getPosition().getX() + ", " + item.getPosition().getY() + ")");
                }
            }
        }
        
        return pickedUpItems;
    }
    
    /**
     * 获取所有未拾取的道具
     */
    public List<Item> getActiveItems() {
        List<Item> activeItems = new ArrayList<>();
        for (Item item : items) {
            if (!item.isCollected()) {
                activeItems.add(item);
            }
        }
        return activeItems;
    }
    
    /**
     * 获取所有道具（包括已拾取的）
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * 移除指定位置的道具
     */
    public boolean removeItemAt(Point2D position) {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getPosition().equals(position)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * 清除所有道具
     */
    public void clearAllItems() {
        items.clear();
    }
    
    /**
     * 获取道具数量
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * 更新道具管理器（可以用于道具消失等逻辑）
     */
    public void update(long currentTimeMs) {
        // 可以在这里添加道具消失逻辑，比如道具存在时间限制
        // 目前道具会一直存在直到被拾取
    }
}
