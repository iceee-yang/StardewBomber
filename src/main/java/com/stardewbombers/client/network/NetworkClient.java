package com.stardewbombers.client.network;

import com.stardewbombers.shared.protocol.*;
import com.stardewbombers.shared.util.JsonUtil;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class NetworkClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageHandler messageHandler;
    private boolean connected = false;
    private Thread receiveThread;
    private String clientId;

    public void connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            clientId = "client_" + UUID.randomUUID().toString().substring(0, 8);

            // 启动接收消息线程
            startReceiveThread();

            // 发送加入游戏的消息
            PlayerJoinMessage joinMsg = new PlayerJoinMessage(clientId, "Player_" + clientId);
            sendMessage(joinMsg);

            System.out.println("已连接到服务器: " + serverAddress + ":" + port);

        } catch (IOException e) {
            System.err.println("连接服务器失败: " + e.getMessage());
            connected = false;
        }
    }

    public void sendMessage(Message message) {
        if (connected && out != null) {
            String json = JsonUtil.toJson(message);
            out.println(json);
            System.out.println("发送消息: " + message.getType());
        }
    }

    private void startReceiveThread() {
        receiveThread = new Thread(() -> {
            try {
                String receivedMessage;
                while (connected && (receivedMessage = in.readLine()) != null) {
                    final String finalMessage = receivedMessage;

                    // 在JavaFX线程中处理消息
                    Platform.runLater(() -> {
                        handleReceivedMessage(finalMessage);
                    });
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("接收消息出错: " + e.getMessage());
                }
            }
        });
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void handleReceivedMessage(String json) {
        try {
            // 这里简化处理，实际应该根据消息类型创建不同的对象
            System.out.println("收到消息: " + json);

            // 通知消息处理器
            if (messageHandler != null) {
                messageHandler.handleMessage(json);
            }

        } catch (Exception e) {
            System.err.println("处理消息失败: " + e.getMessage());
        }
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getClientId() {
        return clientId;
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("断开连接出错: " + e.getMessage());
        }
    }
}
