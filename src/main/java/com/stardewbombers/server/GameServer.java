package com.stardewbombers.server;

import com.stardewbombers.server.network.NetworkServer;
import java.util.Scanner;

public class GameServer {
    public static void main(String[] args) {
        NetworkServer server = new NetworkServer();
        server.start(8888);

        System.out.println("游戏服务器已启动！");
        System.out.println("输入 'quit' 停止服务器");

        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().equals("quit")) {
            // 等待输入
        }

        server.stop();
        scanner.close();
        System.out.println("服务器已停止");
    }
}
