package com.stardewbombers.client;

import java.util.Scanner;

/**
 * 音乐系统测试程序
 * 用于测试背景音乐播放功能
 */
public class MusicTest {
    public static void main(String[] args) {
        System.out.println("=== StardewBombers 音乐系统测试 ===");
        
        // 添加关闭钩子，确保程序退出时清理资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n程序退出，清理音乐资源...");
            MusicManager.cleanup();
        }));
        
        // 初始化音乐管理器
        MusicManager.initialize();
        
        // 开始播放背景音乐
        MusicManager.startBackgroundMusic();
        
        // 创建控制台输入
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n音乐控制命令:");
        System.out.println("1 - 播放音乐");
        System.out.println("2 - 暂停音乐");
        System.out.println("3 - 停止音乐");
        System.out.println("4 - 增加音量");
        System.out.println("5 - 减少音量");
        System.out.println("6 - 切换音乐开关");
        System.out.println("0 - 退出");
        
        while (true) {
            System.out.print("\n请输入命令 (0-6): ");
            String input = scanner.nextLine().trim();
            
            try {
                int command = Integer.parseInt(input);
                
                switch (command) {
                    case 1:
                        MusicManager.startBackgroundMusic();
                        break;
                    case 2:
                        MusicManager.pauseBackgroundMusic();
                        break;
                    case 3:
                        MusicManager.stopBackgroundMusic();
                        break;
                    case 4:
                        double currentVol = MusicManager.getMusicVolume();
                        MusicManager.setMusicVolume(Math.min(1.0, currentVol + 0.1));
                        break;
                    case 5:
                        double currentVol2 = MusicManager.getMusicVolume();
                        MusicManager.setMusicVolume(Math.max(0.0, currentVol2 - 0.1));
                        break;
                    case 6:
                        MusicManager.setMusicEnabled(!MusicManager.isMusicEnabled());
                        break;
                    case 0:
                        System.out.println("退出音乐测试...");
                        MusicManager.cleanup();
                        scanner.close();
                        return;
                    default:
                        System.out.println("无效命令，请输入0-6之间的数字");
                }
                
                // 显示当前状态
                System.out.println("当前状态: " + MusicManager.getMusicStatus());
                
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字命令");
            }
        }
    }
}