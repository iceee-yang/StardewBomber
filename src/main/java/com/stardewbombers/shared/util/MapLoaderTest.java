package com.stardewbombers.shared.util;

import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.enums.BlockType;

/**
 * 地图加载器测试类
 * 用于测试地图加载功能
 */
public class MapLoaderTest {
    
    public static void main(String[] args) {
        System.out.println("=== 地图加载器测试 ===");
        
        // 测试1: 创建默认地图
        System.out.println("\n1. 测试创建默认地图:");
        GameMap defaultMap = MapLoader.createDefaultMap();
        System.out.println("地图大小: " + defaultMap.getWidth() + "x" + defaultMap.getHeight());
        System.out.println("方块大小: " + defaultMap.getTileWidth() + "x" + defaultMap.getTileHeight());
        System.out.println("可破坏方块数量: " + defaultMap.getDestructibleBlocks().size());
        System.out.println("\n地图布局:");
        System.out.println(defaultMap.getMapString());
        
        // 测试2: 测试方块操作
        System.out.println("\n2. 测试方块操作:");
        testBlockOperations(defaultMap);
        
        // 测试3: 尝试加载JSON地图
        System.out.println("\n3. 测试加载JSON地图:");
        try {
            GameMap jsonMap = MapLoader.loadMap("farm_map");
            System.out.println("成功加载JSON地图!");
            System.out.println("地图大小: " + jsonMap.getWidth() + "x" + jsonMap.getHeight());
            System.out.println("可破坏方块数量: " + jsonMap.getDestructibleBlocks().size());
            System.out.println("\nJSON地图布局:");
            System.out.println(jsonMap.getMapString());
        } catch (Exception e) {
            System.out.println("加载JSON地图失败: " + e.getMessage());
        }
        
        // 测试4: 测试瓦片ID映射
        System.out.println("\n4. 测试瓦片ID映射:");
        testTileIdMapping();
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
        MapLoader.getTileIdMapping().forEach((id, type) -> {
            System.out.println("ID " + id + " -> " + type.getName() + 
                             " (solid=" + type.isSolid() + 
                             ", explorable=" + type.isExplorable() + 
                             ", treat=" + type.isTreat() + ")");
        });
    }
}
