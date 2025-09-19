package com.stardewbombers.server.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private String clientId;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetworkServer server;
    private boolean connected = true;

    public ClientHandler(String clientId, Socket socket, NetworkServer server) {
        this.clientId = clientId;
        this.socket = socket;
        this.server = server;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("初始化客户端处理器失败: " + e.getMessage());
            disconnect();
        }
    }

    @Override
    public void run() {
        try {
            String receivedMessage;
            while (connected && (receivedMessage = in.readLine()) != null) {
                // 交给服务器处理
                server.handleClientMessage(clientId, receivedMessage);
            }
        } catch (IOException e) {
            System.err.println("客户端 " + clientId + " 通信错误: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String json) {
        if (connected && out != null) {
            out.println(json);
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("关闭客户端连接出错: " + e.getMessage());
        }
        server.removeClient(clientId);
    }
}
