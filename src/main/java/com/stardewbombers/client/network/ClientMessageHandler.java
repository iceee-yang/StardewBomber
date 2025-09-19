package com.stardewbombers.client.network;

import com.stardewbombers.client.controller.GameController;

public class ClientMessageHandler implements MessageHandler {
    private GameController gameController;

    public ClientMessageHandler(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public void handleMessage(String json) {
        // 解析消息并处理
        System.out.println("处理收到的消息: " + json);

        // 这里可以根据消息类型做不同处理
        // 比如更新玩家位置、显示爆炸效果等
        if (gameController != null) {
            gameController.handleNetworkMessage(json);
        }
    }
}