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
 * 简单的农场服务实现
 */
public class SimpleFarmService implements FarmService {
    private final Map<String, Farm> farms = new HashMap<>();
    private final Map<String, Map<Farm.CropType, Integer>> seedInventory = new HashMap<>();
    private final Map<String, Map<Farm.CropType, Integer>> cropInventory = new HashMap<>();
    
    // 添加农场数据持久化存储
    private final Map<String, FarmData> farmDataStorage = new HashMap<>();
    
    // 添加库存数据持久化存储
    private final Map<String, Map<Farm.CropType, Integer>> savedSeedInventory = new HashMap<>();
    private final Map<String, Map<Farm.CropType, Integer>> savedCropInventory = new HashMap<>();
    
    // 添加金币存储
    private final Map<String, Integer> goldStorage = new HashMap<>();
    private final Map<String, Integer> savedGoldStorage = new HashMap<>();
    
    // 角色解锁管理器
    private final CharacterUnlockManager unlockManager = CharacterUnlockManager.getInstance();
    
    private static final int DEFAULT_FARM_WIDTH = 8;
    private static final int DEFAULT_FARM_HEIGHT = 6;
    
    // 数据文件路径
    private static final String DATA_DIR = "farm_data";
    private static final String FARM_DATA_FILE = DATA_DIR + "/farm_data.txt";
    private static final String INVENTORY_DATA_FILE = DATA_DIR + "/inventory_data.txt";
    
    public SimpleFarmService() {
        // 初始化默认农场数据
        loadAllData();
    }
    
    @Override
    public Farm getFarm(String playerId) {
        return farms.computeIfAbsent(playerId, id -> {
            // 尝试从存储中加载农场数据
            FarmData savedFarmData = farmDataStorage.get(id);
            Farm farm;
            
            if (savedFarmData != null) {
                // 从保存的数据重建农场
                farm = Farm.fromFarmData(savedFarmData);
            } else {
                // 创建新农场
                farm = new Farm(DEFAULT_FARM_WIDTH, DEFAULT_FARM_HEIGHT);
            }
            
            // 给新玩家一些初始种子（如果还没有库存数据）
            if (!seedInventory.containsKey(id)) {
                // 尝试从保存的数据加载库存
                if (savedSeedInventory.containsKey(id)) {
                    seedInventory.put(id, new HashMap<>(savedSeedInventory.get(id)));
                    cropInventory.put(id, new HashMap<>(savedCropInventory.get(id)));
                } else {
                    initializePlayerInventory(id);
                }
            }
            
            // 初始化金币（如果还没有金币数据）
            if (!goldStorage.containsKey(id)) {
                if (savedGoldStorage.containsKey(id)) {
                    goldStorage.put(id, savedGoldStorage.get(id));
                } else {
                    goldStorage.put(id, 0); // 初始金币为0
                }
            }
            
            return farm;
        });
    }
    
    private void initializePlayerInventory(String playerId) {
        // 初始化种子库存
        Map<Farm.CropType, Integer> seeds = new HashMap<>();
        seeds.put(Farm.CropType.CARROT, 5);
        seeds.put(Farm.CropType.STRAWBERRY, 3);
        seeds.put(Farm.CropType.POTATO, 4);
        seedInventory.put(playerId, seeds);
        
        // 初始化作物库存
        Map<Farm.CropType, Integer> crops = new HashMap<>();
        crops.put(Farm.CropType.CARROT, 0);
        crops.put(Farm.CropType.STRAWBERRY, 0);
        crops.put(Farm.CropType.POTATO, 0);
        cropInventory.put(playerId, crops);
    }
    
    @Override
    public boolean plantSeed(String playerId, int x, int y, Farm.CropType cropType) {
        Farm farm = getFarm(playerId);
        
        // 检查是否有足够的种子
        if (getSeedCount(playerId, cropType) <= 0) {
            return false;
        }
        
        // 种植
        if (farm.plantCrop(x, y, cropType)) {
            // 消耗种子
            addSeed(playerId, cropType, -1);
            // 保存农场数据
            saveFarmData(playerId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean waterCrop(String playerId, int x, int y) {
        Farm farm = getFarm(playerId);
        boolean result = farm.waterCrop(x, y);
        if (result) {
            // 保存农场数据
            saveFarmData(playerId);
        }
        return result;
    }
    
    @Override
    public Farm.CropType harvestCrop(String playerId, int x, int y) {
        Farm farm = getFarm(playerId);
        Farm.CropType cropType = farm.harvestCrop(x, y);
        
        if (cropType != Farm.CropType.NONE) {
            // 添加到作物库存
            addCrop(playerId, cropType, 1);
            
            // 根据作物类型给予金币
            int goldReward = getGoldReward(cropType);
            addGold(playerId, goldReward);
            
            // 保存农场数据
            saveFarmData(playerId);
        }
        
        return cropType;
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
    
    @Override
    public boolean clearDeadCrop(String playerId, int x, int y) {
        Farm farm = getFarm(playerId);
        Farm.CropState state = farm.getCropState(x, y);
        
        if (state == Farm.CropState.DEAD) {
            // 清理枯死的作物
            farm.plantCrop(x, y, Farm.CropType.NONE); // 重置为空地
            // 保存农场数据
            saveFarmData(playerId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public void updateFarm(String playerId) {
        Farm farm = getFarm(playerId);
        farm.updateCropStates();
        // 更新后保存农场数据
        saveFarmData(playerId);
    }
    
    @Override
    public int getSeedCount(String playerId, Farm.CropType cropType) {
        Map<Farm.CropType, Integer> seeds = seedInventory.get(playerId);
        if (seeds == null) {
            initializePlayerInventory(playerId);
            seeds = seedInventory.get(playerId);
        }
        return seeds.getOrDefault(cropType, 0);
    }
    
    @Override
    public int getCropCount(String playerId, Farm.CropType cropType) {
        Map<Farm.CropType, Integer> crops = cropInventory.get(playerId);
        if (crops == null) {
            initializePlayerInventory(playerId);
            crops = cropInventory.get(playerId);
        }
        return crops.getOrDefault(cropType, 0);
    }
    
    @Override
    public void addSeed(String playerId, Farm.CropType cropType, int amount) {
        // 确保玩家库存已初始化
        if (!seedInventory.containsKey(playerId)) {
            initializePlayerInventory(playerId);
        }
        
        Map<Farm.CropType, Integer> seeds = seedInventory.get(playerId);
        int currentAmount = seeds.getOrDefault(cropType, 0);
        seeds.put(cropType, Math.max(0, currentAmount + amount));
        
        // 保存农场数据
        saveFarmData(playerId);
    }
    
    @Override
    public void addCrop(String playerId, Farm.CropType cropType, int amount) {
        // 确保玩家库存已初始化
        if (!cropInventory.containsKey(playerId)) {
            initializePlayerInventory(playerId);
        }
        
        Map<Farm.CropType, Integer> crops = cropInventory.get(playerId);
        int currentAmount = crops.getOrDefault(cropType, 0);
        crops.put(cropType, Math.max(0, currentAmount + amount));
        
        // 保存农场数据
        saveFarmData(playerId);
    }
    
    @Override
    public int getGoldCount(String playerId) {
        return goldStorage.getOrDefault(playerId, 0);
    }
    
    @Override
    public void addGold(String playerId, int amount) {
        int currentGold = goldStorage.getOrDefault(playerId, 0);
        int newGold = Math.max(0, currentGold + amount);
        goldStorage.put(playerId, newGold);
        
        // 检查角色解锁
        if (amount > 0) {
            Set<String> newlyUnlocked = unlockManager.checkAndUnlockCharacters(playerId, newGold);
            if (!newlyUnlocked.isEmpty()) {
                System.out.println("玩家 " + playerId + " 解锁了新角色: " + newlyUnlocked);
                // 这里可以触发UI更新或通知
            }
        }
        
        // 保存农场数据
        saveFarmData(playerId);
    }
    
    /**
     * 保存农场数据
     */
    private void saveFarmData(String playerId) {
        Farm farm = farms.get(playerId);
        if (farm != null) {
            FarmData farmData = new FarmData(farm);
            farmDataStorage.put(playerId, farmData);
        }
        
        // 保存库存数据（无论是否有农场对象都要保存）
        if (seedInventory.containsKey(playerId)) {
            savedSeedInventory.put(playerId, new HashMap<>(seedInventory.get(playerId)));
        }
        if (cropInventory.containsKey(playerId)) {
            savedCropInventory.put(playerId, new HashMap<>(cropInventory.get(playerId)));
        }
        if (goldStorage.containsKey(playerId)) {
            savedGoldStorage.put(playerId, goldStorage.get(playerId));
        }
        
        // 保存到文件
        saveAllData();
    }
    
    /**
     * 获取保存的农场数据
     */
    public FarmData getSavedFarmData(String playerId) {
        return farmDataStorage.get(playerId);
    }
    
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
            try (PrintWriter writer = new PrintWriter(new FileWriter(FARM_DATA_FILE))) {
                for (Map.Entry<String, FarmData> entry : farmDataStorage.entrySet()) {
                    writer.println(entry.getKey() + ":" + entry.getValue().toJson());
                }
            }
            
            // 保存库存数据
            try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_DATA_FILE))) {
                // 保存种子库存
                for (Map.Entry<String, Map<Farm.CropType, Integer>> entry : savedSeedInventory.entrySet()) {
                    String playerId = entry.getKey();
                    Map<Farm.CropType, Integer> seeds = entry.getValue();
                    StringBuilder line = new StringBuilder("SEEDS:" + playerId);
                    for (Map.Entry<Farm.CropType, Integer> seedEntry : seeds.entrySet()) {
                        line.append(":").append(seedEntry.getKey()).append(":").append(seedEntry.getValue());
                    }
                    writer.println(line.toString());
                }
                
                // 保存作物库存
                for (Map.Entry<String, Map<Farm.CropType, Integer>> entry : savedCropInventory.entrySet()) {
                    String playerId = entry.getKey();
                    Map<Farm.CropType, Integer> crops = entry.getValue();
                    StringBuilder line = new StringBuilder("CROPS:" + playerId);
                    for (Map.Entry<Farm.CropType, Integer> cropEntry : crops.entrySet()) {
                        line.append(":").append(cropEntry.getKey()).append(":").append(cropEntry.getValue());
                    }
                    writer.println(line.toString());
                }
                
                // 保存金币数据
                for (Map.Entry<String, Integer> entry : savedGoldStorage.entrySet()) {
                    String playerId = entry.getKey();
                    int gold = entry.getValue();
                    writer.println("GOLD:" + playerId + ":" + gold);
                }
            }
            
        } catch (IOException e) {
            System.err.println("保存农场数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置玩家库存到初始状态
     */
    public void resetPlayerInventory(String playerId) {
        // 重置种子库存
        Map<Farm.CropType, Integer> seeds = new HashMap<>();
        seeds.put(Farm.CropType.CARROT, 5);
        seeds.put(Farm.CropType.STRAWBERRY, 3);
        seeds.put(Farm.CropType.POTATO, 4);
        seedInventory.put(playerId, seeds);
        
        // 重置作物库存
        Map<Farm.CropType, Integer> crops = new HashMap<>();
        crops.put(Farm.CropType.CARROT, 0);
        crops.put(Farm.CropType.STRAWBERRY, 0);
        crops.put(Farm.CropType.POTATO, 0);
        cropInventory.put(playerId, crops);
        
        // 重置金币
        goldStorage.put(playerId, 0);
        
        // 保存重置后的数据
        saveAllData();
    }
    
    /**
     * 获取玩家已解锁的角色
     */
    public Set<String> getUnlockedCharacters(String playerId) {
        return unlockManager.getUnlockedCharacters(playerId);
    }
    
    /**
     * 检查角色是否已解锁
     */
    public boolean isCharacterUnlocked(String playerId, String characterName) {
        return unlockManager.isCharacterUnlocked(playerId, characterName);
    }
    
    /**
     * 获取角色解锁所需金币
     */
    public int getRequiredGoldForCharacter(String characterName) {
        return unlockManager.getRequiredGoldForCharacter(characterName);
    }
    
    /**
     * 从文件加载所有数据
     */
    private void loadAllData() {
        try {
            // 加载农场数据
            Path farmDataPath = Paths.get(FARM_DATA_FILE);
            if (Files.exists(farmDataPath)) {
                try (BufferedReader reader = Files.newBufferedReader(farmDataPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":", 2);
                        if (parts.length == 2) {
                            String playerId = parts[0];
                            String jsonData = parts[1];
                            FarmData farmData = FarmData.fromJson(jsonData);
                            farmDataStorage.put(playerId, farmData);
                        }
                    }
                }
            }
            
            // 加载库存数据
            Path inventoryDataPath = Paths.get(INVENTORY_DATA_FILE);
            if (Files.exists(inventoryDataPath)) {
                try (BufferedReader reader = Files.newBufferedReader(inventoryDataPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts.length >= 3) {
                            String type = parts[0];
                            String playerId = parts[1];
                            
                            if ("SEEDS".equals(type)) {
                                Map<Farm.CropType, Integer> seeds = new HashMap<>();
                                for (int i = 2; i < parts.length; i += 2) {
                                    if (i + 1 < parts.length) {
                                        try {
                                            Farm.CropType cropType = Farm.CropType.valueOf(parts[i]);
                                            int amount = Integer.parseInt(parts[i + 1]);
                                            seeds.put(cropType, amount);
                                        } catch (Exception e) {
                                            System.err.println("解析种子数据失败: " + e.getMessage());
                                        }
                                    }
                                }
                                savedSeedInventory.put(playerId, seeds);
                            } else if ("CROPS".equals(type)) {
                                Map<Farm.CropType, Integer> crops = new HashMap<>();
                                for (int i = 2; i < parts.length; i += 2) {
                                    if (i + 1 < parts.length) {
                                        try {
                                            Farm.CropType cropType = Farm.CropType.valueOf(parts[i]);
                                            int amount = Integer.parseInt(parts[i + 1]);
                                            crops.put(cropType, amount);
                                        } catch (Exception e) {
                                            System.err.println("解析作物数据失败: " + e.getMessage());
                                        }
                                    }
                                }
                                savedCropInventory.put(playerId, crops);
                            } else if ("GOLD".equals(type)) {
                                try {
                                    int gold = Integer.parseInt(parts[2]);
                                    savedGoldStorage.put(playerId, gold);
                                } catch (Exception e) {
                                    System.err.println("解析金币数据失败: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("加载农场数据失败: " + e.getMessage());
        }
    }
}
