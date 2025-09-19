package com.stardewbombers.shared.util;

import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.enums.BlockType;

/**
 * 简化的地图测试类
 * 用于测试地图加载功能，不依赖外部库
 */
public class SimpleMapTest {
    
    public static void main(String[] args) {
        System.out.println("=== 地图加载器测试 ===");
        
        // 测试1: 创建默认地图
        System.out.println("\n1. 测试创建默认地图:");
        GameMap defaultMap = SimpleMapLoader.createDefaultMap();
        System.out.println("地图大小: " + defaultMap.getWidth() + "x" + defaultMap.getHeight());
        System.out.println("方块大小: " + defaultMap.getTileWidth() + "x" + defaultMap.getTileHeight());
        System.out.println("可破坏方块数量: " + defaultMap.getDestructibleBlocks().size());
        System.out.println("\n地图布局:");
        System.out.println(defaultMap.getMapString());
        
        // 测试2: 测试方块操作
        System.out.println("\n2. 测试方块操作:");
        testBlockOperations(defaultMap);
        
        // 测试3: 测试瓦片ID映射
        System.out.println("\n3. 测试瓦片ID映射:");
        testTileIdMapping();
        
        // 测试4: 手动创建地图测试
        System.out.println("\n4. 手动创建地图测试:");
        testManualMapCreation();
        
        // 测试5: 测试您的实际Tiled地图
        System.out.println("\n5. 测试您的实际Tiled地图:");
        testFarmMap();
    }
    
    private static void testBlockOperations(GameMap map) {
        // 测试获取方块
        Block block = map.getBlock(5, 5);
        if (block != null) {
            System.out.println("位置(5,5)的方块: " + block.getType().getName());
            System.out.println("是否可通行: " + block.isWalkable());
            System.out.println("是否可破坏: " + block.isDestructible());
        }
        
        // 测试破坏方块
        boolean destroyed = map.destroyBlock(5, 5);
        System.out.println("破坏方块结果: " + destroyed);
        
        // 测试位置检查
        System.out.println("位置(5,5)是否可通行: " + map.isWalkable(5, 5));
        System.out.println("位置(0,0)是否可通行: " + map.isWalkable(0, 0));
    }
    
    private static void testTileIdMapping() {
        System.out.println("当前瓦片ID映射:");
        SimpleMapLoader.getTileIdMapping().forEach((id, type) -> {
            System.out.println("ID " + id + " -> " + type.getName() + 
                             " (solid=" + type.isSolid() + 
                             ", explorable=" + type.isExplorable() + 
                             ", treat=" + type.isTreat() + ")");
        });
    }
    
    private static void testManualMapCreation() {
        // 手动创建一个15x13的地图，模拟您的Tiled地图
        GameMap map = new GameMap(15, 13, 40, 40);
        
        // 设置一些测试方块
        map.setBlock(0, 0, BlockType.BUSHES);   // ID 14
        map.setBlock(1, 0, BlockType.BUSHES);   // ID 17
        map.setBlock(0, 1, BlockType.PUMPKIN);  // ID 34
        map.setBlock(2, 1, BlockType.MELON);    // ID 35
        map.setBlock(6, 1, BlockType.STUMP);    // ID 127
        
        System.out.println("手动创建的地图:");
        System.out.println("地图大小: " + map.getWidth() + "x" + map.getHeight());
        System.out.println("可破坏方块数量: " + map.getDestructibleBlocks().size());
        
        // 显示前几行
        System.out.println("\n地图前3行:");
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 15; x++) {
                Block block = map.getBlock(x, y);
                if (block != null && block.getType() != BlockType.FLOOR) {
                    switch (block.getType()) {
                        case BUSHES:
                            System.out.print("B");
                            break;
                        case PUMPKIN:
                            System.out.print("P");
                            break;
                        case MELON:
                            System.out.print("M");
                            break;
                        case STUMP:
                            System.out.print("S");
                            break;
                        default:
                            System.out.print(" ");
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    private static void testFarmMap() {
        // 创建您的实际Tiled地图
        GameMap farmMap = SimpleMapLoader.createFarmMap();
        
        System.out.println("农场地图信息:");
        System.out.println("地图大小: " + farmMap.getWidth() + "x" + farmMap.getHeight());
        System.out.println("方块大小: " + farmMap.getTileWidth() + "x" + farmMap.getTileHeight());
        System.out.println("可破坏方块数量: " + farmMap.getDestructibleBlocks().size());
        
        // 统计各种方块类型
        int floorCount = 0, bushCount = 0, pumpkinCount = 0, melonCount = 0, stumpCount = 0;
        for (int y = 0; y < farmMap.getHeight(); y++) {
            for (int x = 0; x < farmMap.getWidth(); x++) {
                Block block = farmMap.getBlock(x, y);
                if (block != null) {
                    switch (block.getType()) {
                        case FLOOR:
                            floorCount++;
                            break;
                        case BUSHES:
                            bushCount++;
                            break;
                        case PUMPKIN:
                            pumpkinCount++;
                            break;
                        case MELON:
                            melonCount++;
                            break;
                        case STUMP:
                            stumpCount++;
                            break;
                    }
                }
            }
        }
        
        System.out.println("\n方块统计:");
        System.out.println("地板: " + floorCount);
        System.out.println("灌木: " + bushCount);
        System.out.println("南瓜: " + pumpkinCount);
        System.out.println("瓜: " + melonCount);
        System.out.println("木桩: " + stumpCount);
        
        System.out.println("\n地图布局 (前5行):");
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < farmMap.getWidth(); x++) {
                Block block = farmMap.getBlock(x, y);
                if (block != null && block.getType() != BlockType.FLOOR) {
                    switch (block.getType()) {
                        case BUSHES:
                            System.out.print("B");
                            break;
                        case PUMPKIN:
                            System.out.print("P");
                            break;
                        case MELON:
                            System.out.print("M");
                            break;
                        case STUMP:
                            System.out.print("S");
                            break;
                        default:
                            System.out.print(" ");
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        
        // 测试一些方块操作
        System.out.println("\n测试方块操作:");
        System.out.println("位置(1,0)是否可通行: " + farmMap.isWalkable(1, 0));
        System.out.println("位置(0,1)是否可通行: " + farmMap.isWalkable(0, 1));
        System.out.println("位置(6,1)是否可破坏: " + farmMap.isDestructible(6, 1));
        System.out.println("位置(0,1)是否可破坏: " + farmMap.isDestructible(0, 1));
    }
}
