package com.stardewbombers.server.network;

import com.stardewbombers.shared.protocol.Message;
import com.stardewbombers.shared.util.JsonUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkServer {
    private ServerSocket serverSocket;
    private boolean running = false;
    private Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private Thread serverThread;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;

            System.out.println("服务器启动，监听端口: " + port);

            // 启动服务器监听线程
            serverThread = new Thread(this::acceptClients);
            serverThread.start();

        } catch (IOException e) {
            System.err.println("启动服务器失败: " + e.getMessage());
        }
    }

    private void acceptClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientId = "client_" + System.currentTimeMillis();

                // 为每个客户端创建处理器
                ClientHandler handler = new ClientHandler(clientId, clientSocket, this);
                clients.put(clientId, handler);

                // 启动客户端处理线程
                Thread clientThread = new Thread(handler);
                clientThread.start();

                System.out.println("新客户端连接: " + clientId);

            } catch (IOException e) {
                if (running) {
                    System.err.println("接受客户端连接出错: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastMessage(Message message) {
        String json = JsonUtil.toJson(message);

        for (ClientHandler client : clients.values()) {
            client.sendMessage(json);
        }

        System.out.println("广播消息给 " + clients.size() + " 个客户端");
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
        System.out.println("移除客户端: " + clientId);
    }

    public void handleClientMessage(String clientId, String messageJson) {
        System.out.println("收到客户端 " + clientId + " 的消息: " + messageJson);

        // 转发给其他客户端
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(clientId)) {
                entry.getValue().sendMessage(messageJson);
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("关闭服务器出错: " + e.getMessage());
        }
    }
}
