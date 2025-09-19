package com.stardewbombers.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 简单的JavaFX测试程序
 */
public class SimpleJavaFXTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println("SimpleJavaFXTest: 开始启动...");
        
        try {
            Label label = new Label("JavaFX测试程序\n如果看到这个窗口，说明JavaFX正常工作\n\n按任意键或关闭窗口退出");
            label.setStyle("-fx-font-size: 16px; -fx-padding: 20px; -fx-alignment: center;");
            
            StackPane root = new StackPane();
            root.getChildren().add(label);
            
            Scene scene = new Scene(root, 400, 300);
            
            primaryStage.setTitle("JavaFX测试 - 简单版本");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            // 添加关闭事件处理
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("SimpleJavaFXTest: 窗口关闭事件触发");
                Platform.exit();
            });
            
            System.out.println("SimpleJavaFXTest: 显示窗口...");
            primaryStage.show();
            System.out.println("SimpleJavaFXTest: 窗口已显示");
            
            // 添加键盘事件
            scene.setOnKeyPressed(event -> {
                System.out.println("SimpleJavaFXTest: 按键事件: " + event.getCode());
                if (event.getCode().toString().equals("ESCAPE")) {
                    System.out.println("SimpleJavaFXTest: 按ESC键退出");
                    Platform.exit();
                }
            });
            
            // 确保场景有焦点
            scene.getRoot().requestFocus();
            
        } catch (Exception e) {
            System.err.println("SimpleJavaFXTest: 启动异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("SimpleJavaFXTest: 主方法开始...");
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("SimpleJavaFXTest: 主方法异常: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("SimpleJavaFXTest: 主方法结束");
    }
}
