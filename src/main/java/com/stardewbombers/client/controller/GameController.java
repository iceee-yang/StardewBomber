package com.stardewbombers.client.controller;

public class GameController {

    public void handleNetworkMessage(String json) {
        System.out.println("游戏控制器处理网络消息: " + json);

        // 这里解析消息并更新游戏状态
        // 比如：
        // - 更新其他玩家位置
        // - 显示爆炸效果
        // - 更新农场状态
    }

    public void sendPlayerMove(String direction) {
        // 这里会调用 NetworkClient 发送移动消息
        System.out.println("玩家移动: " + direction);
    }
}
