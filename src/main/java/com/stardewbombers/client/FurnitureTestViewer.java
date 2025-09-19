package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.util.SimpleMapLoader;

/**
 * 家具测试查看器
 * 专门用于测试新的家具类方块
 */
public class FurnitureTestViewer {
    
    // ANSI颜色代码
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";    // 灌木
    private static final String ORANGE = "\u001B[33m";   // 南瓜
    private static final String LIGHT_GREEN = "\u001B[92m"; // 瓜
    private static final String BROWN = "\u001B[38;5;94m"; // 木桩
    private static final String GRAY = "\u001B[37m";     // 地板
    private static final String BLUE = "\u001B[34m";     // 边框
    private static final String DARK_RED = "\u001B[38;5;88m"; // 柜子
    private static final String DARK_YELLOW = "\u001B[38;5;94m"; // 桌子/凳子/椅子
    private static final String RED = "\u001B[38;5;196m"; // 壁炉
    private static final String ORANGE_RUG = "\u001B[38;5;208m"; // 地毯
    
    public static void main(String[] args) {
        System.out.println("=== StardewBombers 家具测试查看器 ===");
        
        // 加载家具测试地图
        GameMap gameMap = SimpleMapLoader.createFurnitureTestMap();
        
        // 显示地图信息
        displayMapInfo(gameMap);
        
        // 显示地图
        displayMap(gameMap);
        
        // 显示图例
        displayLegend();
        
        // 显示统计信息
        displayStatistics(gameMap);
        
        // 显示属性测试
        displayPropertyTest(gameMap);
    }
    
    private static void displayMapInfo(GameMap gameMap) {
        System.out.println("\n" + BLUE + "地图信息:" + RESET);
        System.out.println("大小: " + gameMap.getWidth() + "x" + gameMap.getHeight());
        System.out.println("方块大小: " + gameMap.getTileWidth() + "x" + gameMap.getTileHeight());
        System.out.println("可破坏方块数量: " + gameMap.getDestructibleBlocks().size());
    }
    
    private static void displayMap(GameMap gameMap) {
        System.out.println("\n" + BLUE + "家具测试地图:" + RESET);
        
        // 显示列号
        System.out.print("   ");
        for (int x = 0; x < gameMap.getWidth(); x++) {
            System.out.print(String.format("%2d", x % 10));
        }
        System.out.println();
        
        // 显示地图内容
        for (int y = 0; y < gameMap.getHeight(); y++) {
            System.out.print(String.format("%2d ", y));
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Block block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != BlockType.FLOOR) {
                    System.out.print(getBlockSymbol(block.getType()));
                } else {
                    System.out.print(GRAY + "·" + RESET);
                }
            }
            System.out.println();
        }
    }
    
    private static String getBlockSymbol(BlockType type) {
        switch (type) {
            case CABINET1:
                return DARK_RED + "C" + RESET;
            case CABINET2:
                return DARK_RED + "c" + RESET;
            case TABLE:
                return DARK_YELLOW + "T" + RESET;
            case STOOL:
                return DARK_YELLOW + "t" + RESET;
            case CHAIR:
                return DARK_YELLOW + "H" + RESET;
            case FIREPLACE1:
                return RED + "F" + RESET;
            case FIREPLACE2:
                return RED + "f" + RESET;
            case RUG:
                return ORANGE_RUG + "R" + RESET;
            default:
                return GRAY + "·" + RESET;
        }
    }
    
    private static void displayLegend() {
        System.out.println("\n" + BLUE + "图例:" + RESET);
        System.out.println(DARK_RED + "C/c" + RESET + " - 柜子1/2 (solid=true, explorable=true, treat=true)");
        System.out.println(DARK_YELLOW + "T/t/H" + RESET + " - 桌子/凳子/椅子 (solid=true, explorable=true, treat=false)");
        System.out.println(RED + "F/f" + RESET + " - 壁炉1/2 (solid=true, explorable=false, treat=false)");
        System.out.println(ORANGE_RUG + "R" + RESET + " - 地毯 (solid=false, explorable=false, treat=false)");
        System.out.println(GRAY + "·" + RESET + " - 地板 (solid=false, explorable=false, treat=false)");
    }
    
    private static void displayStatistics(GameMap gameMap) {
        System.out.println("\n" + BLUE + "方块统计:" + RESET);
        
        int floorCount = 0, cabinet1Count = 0, cabinet2Count = 0, tableCount = 0, stoolCount = 0, chairCount = 0;
        int fireplace1Count = 0, fireplace2Count = 0, rugCount = 0;
        
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Block block = gameMap.getBlock(x, y);
                if (block != null) {
                    switch (block.getType()) {
                        case FLOOR:
                            floorCount++;
                            break;
                        case CABINET1:
                            cabinet1Count++;
                            break;
                        case CABINET2:
                            cabinet2Count++;
                            break;
                        case TABLE:
                            tableCount++;
                            break;
                        case STOOL:
                            stoolCount++;
                            break;
                        case CHAIR:
                            chairCount++;
                            break;
                        case FIREPLACE1:
                            fireplace1Count++;
                            break;
                        case FIREPLACE2:
                            fireplace2Count++;
                            break;
                        case RUG:
                            rugCount++;
                            break;
                    }
                }
            }
        }
        
        System.out.println("地板: " + floorCount);
        System.out.println("柜子1: " + cabinet1Count);
        System.out.println("柜子2: " + cabinet2Count);
        System.out.println("桌子: " + tableCount);
        System.out.println("凳子: " + stoolCount);
        System.out.println("椅子: " + chairCount);
        System.out.println("壁炉1: " + fireplace1Count);
        System.out.println("壁炉2: " + fireplace2Count);
        System.out.println("地毯: " + rugCount);
    }
    
    private static void displayPropertyTest(GameMap gameMap) {
        System.out.println("\n" + BLUE + "属性测试:" + RESET);
        
        // 测试每个家具的属性
        BlockType[] furnitureTypes = {
            BlockType.CABINET1, BlockType.CABINET2, BlockType.TABLE, 
            BlockType.STOOL, BlockType.CHAIR, BlockType.FIREPLACE1, 
            BlockType.FIREPLACE2, BlockType.RUG
        };
        
        for (BlockType type : furnitureTypes) {
            System.out.printf("%-12s: solid=%s, explorable=%s, treat=%s, walkable=%s%n",
                type.getName(),
                type.isSolid(),
                type.isExplorable(),
                type.isTreat(),
                type.isWalkable());
        }
    }
}
