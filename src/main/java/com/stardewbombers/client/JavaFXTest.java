package com.stardewbombers.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 简单的JavaFX测试程序
 * 用于验证JavaFX是否可用
 */
public class JavaFXTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("JavaFX 工作正常！");
        StackPane root = new StackPane();
        root.getChildren().add(label);
        
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("JavaFX 测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        System.out.println("尝试启动JavaFX...");
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("JavaFX启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
