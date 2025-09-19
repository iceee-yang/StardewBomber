package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.util.SimpleMapLoader;

/**
 * 控制台地图查看器
 * 在控制台中以彩色文字显示地图
 */
public class ConsoleMapViewer {
    
    // ANSI颜色代码
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";    // 灌木
    private static final String ORANGE = "\u001B[33m";   // 南瓜
    private static final String LIGHT_GREEN = "\u001B[92m"; // 瓜
    private static final String BROWN = "\u001B[38;5;94m"; // 木桩
    private static final String GRAY = "\u001B[37m";     // 地板
    private static final String BLUE = "\u001B[34m";     // 边框
    
    public static void main(String[] args) {
        System.out.println("=== StardewBombers 地图查看器 ===");
        
        // 加载地图
        GameMap gameMap = SimpleMapLoader.createHomeMap();
        
        // 显示地图信息
        displayMapInfo(gameMap);
        
        // 显示地图
        displayMap(gameMap);
        
        // 显示图例
        displayLegend();
        
        // 显示统计信息
        displayStatistics(gameMap);
    }
    
    private static void displayMapInfo(GameMap gameMap) {
        System.out.println("\n" + BLUE + "地图信息:" + RESET);
        System.out.println("大小: " + gameMap.getWidth() + "x" + gameMap.getHeight());
        System.out.println("方块大小: " + gameMap.getTileWidth() + "x" + gameMap.getTileHeight());
        System.out.println("可破坏方块数量: " + gameMap.getDestructibleBlocks().size());
    }
    
    private static void displayMap(GameMap gameMap) {
        System.out.println("\n" + BLUE + "地图布局:" + RESET);
        
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
                if (block != null) {
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
            case FLOOR:
                return GRAY + "·" + RESET;  // 地板
            case BUSHES:
                return GREEN + "B" + RESET;
            case PUMPKIN:
                return ORANGE + "P" + RESET;
            case MELON:
                return LIGHT_GREEN + "M" + RESET;
            case STUMP:
                return BROWN + "S" + RESET;
            case CABINET1:
                return "\u001B[38;5;88m" + "C" + RESET;  // 深红色
            case CABINET2:
                return "\u001B[38;5;88m" + "c" + RESET;  // 深红色小写
            case TABLE:
                return "\u001B[38;5;94m" + "T" + RESET;  // 深黄色
            case STOOL:
                return "\u001B[38;5;94m" + "t" + RESET;  // 深黄色小写
            case CHAIR:
                return "\u001B[38;5;94m" + "H" + RESET;  // 深黄色
            case FIREPLACE1:
                return "\u001B[38;5;196m" + "F" + RESET; // 红色
            case FIREPLACE2:
                return "\u001B[38;5;196m" + "f" + RESET; // 红色小写
            case RUG:
                return "\u001B[38;5;208m" + "R" + RESET; // 橙色
            default:
                return GRAY + "·" + RESET;
        }
    }
    
    private static void displayLegend() {
        System.out.println("\n" + BLUE + "图例:" + RESET);
        System.out.println(GREEN + "B" + RESET + " - 灌木 (可破坏)");
        System.out.println(ORANGE + "P" + RESET + " - 南瓜 (可破坏, 有奖励)");
        System.out.println(LIGHT_GREEN + "M" + RESET + " - 瓜 (可破坏, 有奖励)");
        System.out.println(BROWN + "S" + RESET + " - 木桩 (不可破坏)");
        System.out.println("\u001B[38;5;88m" + "C/c" + RESET + " - 柜子1/2 (可破坏, 有奖励)");
        System.out.println("\u001B[38;5;94m" + "T/t/H" + RESET + " - 桌子/凳子/椅子 (可破坏, 无奖励)");
        System.out.println("\u001B[38;5;196m" + "F/f" + RESET + " - 壁炉1/2 (不可破坏)");
        System.out.println("\u001B[38;5;208m" + "R" + RESET + " - 地毯 (可通行)");
        System.out.println(GRAY + "·" + RESET + " - 地板 (可通行)");
    }
    
    private static void displayStatistics(GameMap gameMap) {
        System.out.println("\n" + BLUE + "方块统计:" + RESET);
        
        int floorCount = 0, bushCount = 0, pumpkinCount = 0, melonCount = 0, stumpCount = 0;
        int cabinet1Count = 0, cabinet2Count = 0, tableCount = 0, stoolCount = 0, chairCount = 0;
        int fireplace1Count = 0, fireplace2Count = 0, rugCount = 0;
        
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Block block = gameMap.getBlock(x, y);
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
        System.out.println("灌木: " + bushCount);
        System.out.println("南瓜: " + pumpkinCount);
        System.out.println("瓜: " + melonCount);
        System.out.println("木桩: " + stumpCount);
        System.out.println("柜子1: " + cabinet1Count);
        System.out.println("柜子2: " + cabinet2Count);
        System.out.println("桌子: " + tableCount);
        System.out.println("凳子: " + stoolCount);
        System.out.println("椅子: " + chairCount);
        System.out.println("壁炉1: " + fireplace1Count);
        System.out.println("壁炉2: " + fireplace2Count);
        System.out.println("地毯: " + rugCount);
        
        // 显示一些特殊位置的信息
        System.out.println("\n" + BLUE + "特殊位置测试:" + RESET);
        testSpecialPositions(gameMap);
    }
    
    private static void testSpecialPositions(GameMap gameMap) {
        // 测试一些有趣的位置
        int[][] testPositions = {{0, 0}, {1, 0}, {0, 1}, {6, 1}, {7, 1}};
        
        for (int[] pos : testPositions) {
            int x = pos[0], y = pos[1];
            Block block = gameMap.getBlock(x, y);
            if (block != null) {
                System.out.printf("位置(%d,%d): %s - 可通行:%s, 可破坏:%s%n",
                    x, y, block.getType().getName(),
                    gameMap.isWalkable(x, y),
                    gameMap.isDestructible(x, y));
            }
        }
    }
}
