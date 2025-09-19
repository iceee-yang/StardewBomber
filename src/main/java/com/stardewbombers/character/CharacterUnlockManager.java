package com.stardewbombers.character;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * 角色解锁管理器
 * 管理角色解锁状态和金币要求
 */
public class CharacterUnlockManager {
    
    // 角色解锁要求（金币数量）
    public static final Map<String, Integer> CHARACTER_UNLOCK_REQUIREMENTS = new HashMap<>();
    static {
        CHARACTER_UNLOCK_REQUIREMENTS.put("Demetrius", 10);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Leah", 30);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Penny", 50);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Sebastian", 100);
    }
    
    // 默认解锁的角色
    public static final Set<String> DEFAULT_UNLOCKED_CHARACTERS = new HashSet<>();
    static {
        DEFAULT_UNLOCKED_CHARACTERS.add("Abigail");
        DEFAULT_UNLOCKED_CHARACTERS.add("Alex");
        DEFAULT_UNLOCKED_CHARACTERS.add("Haley");
        DEFAULT_UNLOCKED_CHARACTERS.add("Lewis");
    }
    
    // 存储每个玩家已解锁的角色
    private final Map<String, Set<String>> unlockedCharacters = new HashMap<>();
    
    // 数据文件路径
    private static final String DATA_DIR = "farm_data";
    private static final String UNLOCK_DATA_FILE = DATA_DIR + "/character_unlock_data.txt";
    
    private static CharacterUnlockManager instance;
    
    private CharacterUnlockManager() {
        loadUnlockData();
    }
    
    public static CharacterUnlockManager getInstance() {
        if (instance == null) {
            instance = new CharacterUnlockManager();
        }
        return instance;
    }
    
    /**
     * 检查角色是否已解锁
     */
    public boolean isCharacterUnlocked(String playerId, String characterName) {
        Set<String> playerUnlocked = unlockedCharacters.get(playerId);
        if (playerUnlocked == null) {
            // 如果是默认解锁的角色，返回true
            return DEFAULT_UNLOCKED_CHARACTERS.contains(characterName);
        }
        return playerUnlocked.contains(characterName) || DEFAULT_UNLOCKED_CHARACTERS.contains(characterName);
    }
    
    /**
     * 获取玩家已解锁的所有角色
     */
    public Set<String> getUnlockedCharacters(String playerId) {
        Set<String> allUnlocked = new HashSet<>(DEFAULT_UNLOCKED_CHARACTERS);
        Set<String> playerUnlocked = unlockedCharacters.get(playerId);
        if (playerUnlocked != null) {
            allUnlocked.addAll(playerUnlocked);
        }
        return allUnlocked;
    }
    
    /**
     * 尝试解锁角色（基于金币数量）
     */
    public boolean tryUnlockCharacter(String playerId, String characterName, int currentGold) {
        // 检查是否已经解锁
        if (isCharacterUnlocked(playerId, characterName)) {
            return false;
        }
        
        // 检查金币要求
        Integer requiredGold = CHARACTER_UNLOCK_REQUIREMENTS.get(characterName);
        if (requiredGold == null || currentGold < requiredGold) {
            return false;
        }
        
        // 解锁角色
        Set<String> playerUnlocked = unlockedCharacters.computeIfAbsent(playerId, k -> new HashSet<>());
        playerUnlocked.add(characterName);
        
        // 保存数据
        saveUnlockData();
        
        return true;
    }
    
    /**
     * 检查并解锁所有符合条件的角色
     * 返回新解锁的角色列表
     */
    public Set<String> checkAndUnlockCharacters(String playerId, int currentGold) {
        Set<String> newlyUnlocked = new HashSet<>();
        
        for (Map.Entry<String, Integer> entry : CHARACTER_UNLOCK_REQUIREMENTS.entrySet()) {
            String characterName = entry.getKey();
            int requiredGold = entry.getValue();
            
            if (!isCharacterUnlocked(playerId, characterName) && currentGold >= requiredGold) {
                Set<String> playerUnlocked = unlockedCharacters.computeIfAbsent(playerId, k -> new HashSet<>());
                playerUnlocked.add(characterName);
                newlyUnlocked.add(characterName);
            }
        }
        
        if (!newlyUnlocked.isEmpty()) {
            saveUnlockData();
        }
        
        return newlyUnlocked;
    }
    
    /**
     * 获取角色解锁所需金币
     */
    public int getRequiredGoldForCharacter(String characterName) {
        return CHARACTER_UNLOCK_REQUIREMENTS.getOrDefault(characterName, 0);
    }
    
    /**
     * 获取角色显示名称
     */
    public String getCharacterDisplayName(String characterName) {
        switch (characterName) {
            case "Abigail": return "阿比盖尔";
            case "Alex": return "亚历克斯";
            case "Haley": return "海莉";
            case "Lewis": return "刘易斯";
            case "Demetrius": return "德米特里";
            case "Leah": return "莉亚";
            case "Penny": return "佩妮";
            case "Sebastian": return "塞巴斯蒂安";
            default: return characterName;
        }
    }
    
    /**
     * 获取角色描述
     */
    public String getCharacterDescription(String characterName) {
        switch (characterName) {
            case "Abigail": return "勇敢的冒险家，喜欢探索";
            case "Alex": return "运动健将，充满活力";
            case "Haley": return "美丽的艺术家，富有创造力";
            case "Lewis": return "经验丰富的镇长，智慧过人";
            case "Demetrius": return "科学家，善于研究";
            case "Leah": return "艺术家，热爱自然";
            case "Penny": return "教师，温柔善良";
            case "Sebastian": return "程序员，神秘内向";
            default: return "神秘的角色";
        }
    }
    
    /**
     * 保存解锁数据到文件
     */
    private void saveUnlockData() {
        try {
            // 创建数据目录
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            // 保存解锁数据
            try (PrintWriter writer = new PrintWriter(new FileWriter(UNLOCK_DATA_FILE))) {
                for (Map.Entry<String, Set<String>> entry : unlockedCharacters.entrySet()) {
                    String playerId = entry.getKey();
                    Set<String> characters = entry.getValue();
                    StringBuilder line = new StringBuilder(playerId);
                    for (String character : characters) {
                        line.append(":").append(character);
                    }
                    writer.println(line.toString());
                }
            }
            
        } catch (IOException e) {
            System.err.println("保存角色解锁数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 从文件加载解锁数据
     */
    private void loadUnlockData() {
        try {
            Path unlockDataPath = Paths.get(UNLOCK_DATA_FILE);
            if (Files.exists(unlockDataPath)) {
                try (BufferedReader reader = Files.newBufferedReader(unlockDataPath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts.length >= 2) {
                            String playerId = parts[0];
                            Set<String> characters = new HashSet<>();
                            for (int i = 1; i < parts.length; i++) {
                                characters.add(parts[i]);
                            }
                            unlockedCharacters.put(playerId, characters);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("加载角色解锁数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置玩家解锁状态（用于测试）
     */
    public void resetPlayerUnlocks(String playerId) {
        unlockedCharacters.remove(playerId);
        saveUnlockData();
    }
}
