package com.stardewbombers.shared.util;

import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化的地图加载器
 * 不依赖外部JSON库，用于基本测试
 */
public class SimpleMapLoader {
    
    // 瓦片ID到BlockType的映射
    private static final Map<Integer, BlockType> TILE_ID_MAPPING = new HashMap<>();
    
    static {
        // 农场地图的瓦片ID映射
        TILE_ID_MAPPING.put(14, BlockType.BUSHES);  // 灌木
        TILE_ID_MAPPING.put(17, BlockType.BUSHES);  // 灌木
        TILE_ID_MAPPING.put(30, BlockType.BUSHES);  // 灌木
        TILE_ID_MAPPING.put(34, BlockType.PUMPKIN); // 南瓜
        TILE_ID_MAPPING.put(35, BlockType.MELON);   // 瓜
        TILE_ID_MAPPING.put(127, BlockType.STUMP);  // 木桩
        
        // 家具地图的瓦片ID映射
        TILE_ID_MAPPING.put(100, BlockType.CABINET1);   // 柜子1
        TILE_ID_MAPPING.put(101, BlockType.CABINET2);   // 柜子2
        TILE_ID_MAPPING.put(102, BlockType.TABLE);      // 桌子
        TILE_ID_MAPPING.put(103, BlockType.STOOL);      // 凳子
        TILE_ID_MAPPING.put(104, BlockType.CHAIR);      // 椅子
        TILE_ID_MAPPING.put(105, BlockType.FIREPLACE1); // 壁炉1
        TILE_ID_MAPPING.put(106, BlockType.FIREPLACE2); // 壁炉2
        TILE_ID_MAPPING.put(107, BlockType.RUG);        // 地毯
    }
    
    /**
     * 设置瓦片ID映射
     */
    public static void setTileIdMapping(int tileId, BlockType blockType) {
        TILE_ID_MAPPING.put(tileId, blockType);
    }
    
    /**
     * 获取当前瓦片ID映射
     */
    public static Map<Integer, BlockType> getTileIdMapping() {
        return new HashMap<>(TILE_ID_MAPPING);
    }
    
    /**
     * 创建默认测试地图
     */
    public static GameMap createDefaultMap() {
        GameMap map = new GameMap(16, 16, 32, 32);
        
        // 创建一些测试方块
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (x == 0 || x == 15 || y == 0 || y == 15) {
                    // 边界使用木桩
                    map.setBlock(x, y, BlockType.STUMP);
                } else if ((x + y) % 3 == 0) {
                    // 一些灌木
                    map.setBlock(x, y, BlockType.BUSHES);
                } else if ((x + y) % 5 == 0) {
                    // 一些南瓜
                    map.setBlock(x, y, BlockType.PUMPKIN);
                } else if ((x + y) % 7 == 0) {
                    // 一些瓜
                    map.setBlock(x, y, BlockType.MELON);
                }
                // 其他位置保持为地板
            }
        }
        
        return map;
    }
    
    /**
     * 根据瓦片ID获取BlockType
     */
    public static BlockType getBlockTypeFromTileId(int tileId) {
        return TILE_ID_MAPPING.getOrDefault(tileId, BlockType.FLOOR);
    }
    
    /**
     * 创建模拟您的Tiled地图
     */
    public static GameMap createFarmMap() {
        GameMap map = new GameMap(15, 13, 40, 40);
        
        // 模拟您的Tiled地图数据
        int[][] mapData = {
            {14, 17, 14, 17, 0, 0, 0, 0, 0, 0, 0, 17, 14, 17, 14},
            {34, 0, 35, 34, 30, 17, 127, 0, 127, 17, 30, 34, 35, 0, 34},
            {0, 0, 127, 127, 127, 17, 127, 127, 127, 17, 127, 127, 127, 0, 0},
            {17, 127, 14, 127, 14, 17, 34, 127, 30, 17, 14, 127, 14, 127, 17},
            {14, 17, 14, 17, 30, 0, 127, 127, 127, 0, 35, 17, 14, 17, 14},
            {17, 127, 0, 17, 14, 17, 14, 14, 14, 17, 14, 17, 0, 127, 17},
            {0, 127, 17, 30, 35, 34, 17, 0, 17, 35, 30, 34, 17, 127, 14},
            {0, 30, 35, 14, 17, 14, 127, 127, 127, 14, 17, 14, 35, 30, 0},
            {0, 35, 14, 17, 30, 0, 0, 127, 0, 0, 30, 17, 14, 35, 0},
            {0, 0, 0, 17, 0, 0, 127, 127, 127, 0, 0, 17, 0, 0, 0},
            {0, 34, 35, 127, 14, 17, 14, 14, 14, 17, 14, 127, 35, 34, 17},
            {0, 35, 30, 14, 127, 127, 17, 127, 17, 127, 127, 14, 30, 35, 0},
            {0, 0, 0, 17, 17, 127, 127, 127, 127, 127, 17, 17, 0, 0, 0}
        };
        
        // 填充地图
        for (int y = 0; y < 13; y++) {
            for (int x = 0; x < 15; x++) {
                int tileId = mapData[y][x];
                BlockType blockType = tileId > 0 ? getBlockTypeFromTileId(tileId) : BlockType.FLOOR;
                map.setBlock(x, y, blockType);
            }
        }
        
        return map;
    }
    
    /**
     * 创建包含新家具的测试地图
     */
    public static GameMap createFurnitureTestMap() {
        GameMap map = new GameMap(12, 10, 40, 40);
        
        // 创建包含新家具的测试地图
        int[][] mapData = {
            {100, 101, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {102, 103, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {105, 106, 107, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        
        // 填充地图
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 12; x++) {
                int tileId = mapData[y][x];
                if (tileId > 0) {
                    BlockType blockType = getBlockTypeFromTileId(tileId);
                    map.setBlock(x, y, blockType);
                }
            }
        }
        
        return map;
    }
    
    /**
     * 从JSON数据创建地图（基于您的home_map.json）
     */
    public static GameMap createHomeMap() {
        GameMap map = new GameMap(15, 13, 40, 40);
        
        // 家具层数据（surface层）
        int[][] furnitureData = {
            {0, 100, 101, 0, 101, 0, 0, 0, 105, 0, 101, 0, 0, 101, 100},
            {100, 106, 106, 0, 101, 106, 101, 106, 105, 103, 105, 102, 105, 100, 102},
            {100, 100, 106, 0, 106, 106, 0, 101, 104, 101, 106, 0, 105, 106, 100},
            {106, 103, 0, 0, 0, 102, 101, 105, 106, 104, 0, 0, 0, 0, 104},
            {0, 0, 105, 0, 105, 100, 0, 102, 0, 0, 105, 0, 106, 105, 0},
            {0, 106, 0, 100, 100, 0, 0, 0, 0, 105, 105, 100, 0, 106, 0},
            {100, 105, 100, 106, 101, 105, 0, 0, 100, 106, 0, 105, 0, 106, 0},
            {0, 0, 103, 0, 0, 101, 101, 0, 100, 103, 0, 101, 0, 101, 104},
            {106, 102, 105, 0, 106, 0, 106, 105, 106, 100, 105, 0, 105, 0, 106},
            {104, 101, 0, 0, 0, 0, 0, 106, 100, 0, 0, 0, 0, 101, 100},
            {106, 0, 106, 0, 106, 106, 0, 102, 101, 0, 106, 103, 106, 105, 0},
            {100, 101, 0, 0, 100, 106, 0, 105, 0, 0, 101, 0, 104, 106, 0},
            {103, 102, 106, 0, 100, 0, 100, 0, 101, 106, 104, 106, 0, 100, 101}
        };
        
        // 根据家具层数据设置地图
        for (int y = 0; y < 13; y++) {
            for (int x = 0; x < 15; x++) {
                int tileId = furnitureData[y][x];
                if (tileId > 0) {
                    // 有家具，设置家具类型
                    BlockType blockType = getBlockTypeFromTileId(tileId);
                    map.setBlock(x, y, blockType);
                } else {
                    // 没有家具，设置为地毯（因为bottom层全部是地毯）
                    map.setBlock(x, y, BlockType.RUG);
                }
            }
        }
        
        return map;
    }
}
