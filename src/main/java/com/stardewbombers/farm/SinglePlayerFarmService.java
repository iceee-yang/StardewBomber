package com.stardewbombers.farm;

import com.stardewbombers.character.CharacterUnlockManager;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单机版农场服务 - 不需要playerId
 */
public class SinglePlayerFarmService implements FarmService {
    private static SinglePlayerFarmService instance;
    
    // 全局数据存储 - 不需要playerId
    private Farm farm;
    private Map<Farm.CropType, Integer> seedInventory = new HashMap<>();
    private Map<Farm.CropType, Integer> cropInventory = new HashMap<>();
    private int goldCount = 0;
    
    // 角色解锁管理器
    private final CharacterUnlockManager unlockManager = CharacterUnlockManager.getInstance();
    
    private static final int DEFAULT_FARM_WIDTH = 8;
    private static final int DEFAULT_FARM_HEIGHT = 6;
    
    // 数据文件路径
    private static final String DATA_DIR = "farm_data";
    private static final String FARM_DATA_FILE = DATA_DIR + "/single_farm_data.txt";
    private static final String INVENTORY_DATA_FILE = DATA_DIR + "/single_inventory_data.txt";
    
    private SinglePlayerFarmService() {
        // 初始化农场
        this.farm = new Farm(DEFAULT_FARM_WIDTH, DEFAULT_FARM_HEIGHT);
        
        // 初始化库存
        initializeInventory();
        
        // 加载保存的数据
        loadAllData();
    }
    
    /**
     * 获取单例实例
     */
    public static SinglePlayerFarmService getInstance() {
        if (instance == null) {
            instance = new SinglePlayerFarmService();
        }
        return instance;
    }
    
    /**
     * 初始化库存
     */
    private void initializeInventory() {
        seedInventory.put(Farm.CropType.CARROT, 5);
        seedInventory.put(Farm.CropType.STRAWBERRY, 3);
        seedInventory.put(Farm.CropType.POTATO, 3);
        
        cropInventory.put(Farm.CropType.CARROT, 0);
        cropInventory.put(Farm.CropType.STRAWBERRY, 0);
        cropInventory.put(Farm.CropType.POTATO, 0);
        
        goldCount = 0;
    }
    
    // ========== 简化的接口方法 - 不需要playerId ==========
    
    @Override
    public Farm getFarm(String playerId) {
        // 忽略playerId，直接返回全局农场
        return farm;
    }
    
    @Override
    public boolean plantSeed(String playerId, int x, int y, Farm.CropType cropType) {
        // 检查种子数量
        if (getSeedCount(cropType) <= 0) {
            return false;
        }
        
        // 种植
        if (farm.plantCrop(x, y, cropType)) {
            // 消耗种子
            addSeed(cropType, -1);
            saveAllData();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean waterCrop(String playerId, int x, int y) {
        return farm.waterCrop(x, y);
    }
    
    @Override
    public Farm.CropType harvestCrop(String playerId, int x, int y) {
        Farm.CropType cropType = farm.harvestCrop(x, y);
        if (cropType != null) {
            addCrop(cropType, 1);
            // 添加金币奖励
            int goldReward = getGoldReward(cropType);
            addGold(goldReward);
            saveAllData();
        }
        return cropType;
    }
    
    @Override
    public boolean clearDeadCrop(String playerId, int x, int y) {
        // 检查位置是否有效且作物已枯死
        if (x >= 0 && x < farm.getWidth() && y >= 0 && y < farm.getHeight() && 
            farm.getCropState(x, y) == Farm.CropState.DEAD) {
            // 清理枯死的作物
            farm.setCropType(x, y, Farm.CropType.NONE);
            farm.setCropState(x, y, Farm.CropState.EMPTY);
            farm.setPlantTime(x, y, null);
            farm.setWaterTime(x, y, null);
            return true;
        }
        return false;
    }
    
    @Override
    public void updateFarm(String playerId) {
        farm.updateCropStates();
    }
    
    // ========== 实现接口方法 ==========
    
    @Override
    public int getSeedCount(String playerId, Farm.CropType cropType) {
        return seedInventory.getOrDefault(cropType, 0);
    }
    
    @Override
    public int getCropCount(String playerId, Farm.CropType cropType) {
        return cropInventory.getOrDefault(cropType, 0);
    }
    
    @Override
    public int getGoldCount(String playerId) {
        return goldCount;
    }
    
    @Override
    public void addSeed(String playerId, Farm.CropType cropType, int amount) {
        int current = seedInventory.getOrDefault(cropType, 0);
        seedInventory.put(cropType, Math.max(0, current + amount));
        saveAllData();
    }
    
    @Override
    public void addCrop(String playerId, Farm.CropType cropType, int amount) {
        int current = cropInventory.getOrDefault(cropType, 0);
        cropInventory.put(cropType, Math.max(0, current + amount));
        saveAllData();
    }
    
    @Override
    public void addGold(String playerId, int amount) {
        goldCount = Math.max(0, goldCount + amount);
        
        // 检查角色解锁
        if (amount > 0) {
            Set<String> newlyUnlocked = unlockManager.checkAndUnlockCharacters("singlePlayer", goldCount);
            if (!newlyUnlocked.isEmpty()) {
                System.out.println("解锁了新角色: " + newlyUnlocked);
            }
        }
        
        saveAllData();
    }
    
    // ========== 数据持久化 ==========
    
    /**
     * 保存所有数据到文件
     */
    public void saveAllData() {
        try {
            // 创建数据目录
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // 保存农场数据
            saveFarmData();
            
            // 保存库存数据
            saveInventoryData();
            
        } catch (Exception e) {
            System.err.println("保存数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 保存农场数据
     */
    private void saveFarmData() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FARM_DATA_FILE))) {
            FarmData farmData = new FarmData(farm);
            writer.println(farmData.toJson());
        }
    }
    
    /**
     * 保存库存数据
     */
    private void saveInventoryData() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_DATA_FILE))) {
            // 保存种子库存
            StringBuilder seedLine = new StringBuilder("SEEDS");
            for (Map.Entry<Farm.CropType, Integer> entry : seedInventory.entrySet()) {
                seedLine.append(":").append(entry.getKey()).append(":").append(entry.getValue());
            }
            writer.println(seedLine.toString());
            
            // 保存作物库存
            StringBuilder cropLine = new StringBuilder("CROPS");
            for (Map.Entry<Farm.CropType, Integer> entry : cropInventory.entrySet()) {
                cropLine.append(":").append(entry.getKey()).append(":").append(entry.getValue());
            }
            writer.println(cropLine.toString());
            
            // 保存金币
            writer.println("GOLD:" + goldCount);
        }
    }
    
    /**
     * 加载所有数据
     */
    private void loadAllData() {
        try {
            loadFarmData();
            loadInventoryData();
        } catch (Exception e) {
            System.err.println("加载数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 加载农场数据
     */
    private void loadFarmData() throws IOException {
        Path farmDataPath = Paths.get(FARM_DATA_FILE);
        if (Files.exists(farmDataPath)) {
            try (BufferedReader reader = Files.newBufferedReader(farmDataPath)) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    FarmData farmData = FarmData.fromJson(line);
                    this.farm = Farm.fromFarmData(farmData);
                }
            }
        }
    }
    
    /**
     * 加载库存数据
     */
    private void loadInventoryData() throws IOException {
        Path inventoryDataPath = Paths.get(INVENTORY_DATA_FILE);
        if (Files.exists(inventoryDataPath)) {
            try (BufferedReader reader = Files.newBufferedReader(inventoryDataPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2) {
                        String type = parts[0];
                        
                        if ("SEEDS".equals(type)) {
                            for (int i = 1; i < parts.length; i += 2) {
                                if (i + 1 < parts.length) {
                                    try {
                                        Farm.CropType cropType = Farm.CropType.valueOf(parts[i]);
                                        int amount = Integer.parseInt(parts[i + 1]);
                                        seedInventory.put(cropType, amount);
                                    } catch (Exception e) {
                                        System.err.println("解析种子数据失败: " + e.getMessage());
                                    }
                                }
                            }
                        } else if ("CROPS".equals(type)) {
                            for (int i = 1; i < parts.length; i += 2) {
                                if (i + 1 < parts.length) {
                                    try {
                                        Farm.CropType cropType = Farm.CropType.valueOf(parts[i]);
                                        int amount = Integer.parseInt(parts[i + 1]);
                                        cropInventory.put(cropType, amount);
                                    } catch (Exception e) {
                                        System.err.println("解析作物数据失败: " + e.getMessage());
                                    }
                                }
                            }
                        } else if ("GOLD".equals(type)) {
                            try {
                                goldCount = Integer.parseInt(parts[1]);
                            } catch (Exception e) {
                                System.err.println("解析金币数据失败: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 重置库存到初始状态
     */
    public void resetInventory() {
        initializeInventory();
        saveAllData();
    }
    
    /**
     * 根据作物类型获取金币奖励
     */
    private int getGoldReward(Farm.CropType cropType) {
        switch (cropType) {
            case CARROT:
                return 1; // 胡萝卜1个金币
            case STRAWBERRY:
                return 2; // 草莓2个金币
            case POTATO:
                return 3; // 土豆3个金币
            default:
                return 0;
        }
    }
}
