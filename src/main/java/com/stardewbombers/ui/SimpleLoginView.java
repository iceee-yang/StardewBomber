package com.stardewbombers.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.stardewbombers.model.Player;
import com.stardewbombers.service.PlayerService;
import com.stardewbombers.service.impl.PlayerServiceImpl;

import java.util.function.Consumer;

/**
 * 简化版登录界面
 * 作为临时解决方案，提供更简洁的登录体验
 */
public class SimpleLoginView {
    private final PlayerService service = new PlayerServiceImpl();
    private final Consumer<Player> onLoginSuccess;

    public SimpleLoginView(Consumer<Player> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void show(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        // 标题
        Label titleLabel = new Label("StardewBombers");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // 登录表单
        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setHgap(10);
        loginForm.setVgap(10);
        loginForm.setPadding(new Insets(20));

        TextField phoneField = new TextField();
        phoneField.setPromptText("手机号");
        phoneField.setPrefWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        passwordField.setPrefWidth(200);

        Button loginButton = new Button("登录");
        loginButton.setPrefWidth(200);
        loginButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");

        Button registerButton = new Button("注册");
        registerButton.setPrefWidth(200);
        registerButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #E74C3C;");

        loginForm.addRow(0, phoneField);
        loginForm.addRow(1, passwordField);
        loginForm.addRow(2, loginButton);
        loginForm.addRow(3, registerButton);
        loginForm.addRow(4, messageLabel);

        // 登录按钮事件
        loginButton.setOnAction(e -> {
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            
            if (phone.isEmpty() || password.isEmpty()) {
                messageLabel.setText("请填写完整信息");
                return;
            }

            boolean success = service.login(phone, password);
            if (success) {
                Player player = service.getProfile(phone);
                messageLabel.setText("登录成功！");
                messageLabel.setStyle("-fx-text-fill: #27AE60;");
                
                if (player != null && onLoginSuccess != null) {
                    onLoginSuccess.accept(player);
                }
            } else {
                messageLabel.setText("手机号或密码错误");
                messageLabel.setStyle("-fx-text-fill: #E74C3C;");
            }
        });

        // 注册按钮事件
        registerButton.setOnAction(e -> {
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();
            
            if (phone.isEmpty() || password.isEmpty()) {
                messageLabel.setText("请填写完整信息");
                return;
            }

            // 生成默认昵称
            String nickname = "玩家" + phone.substring(phone.length() - 4);
            
            boolean success = service.register(phone, nickname, password);
            if (success) {
                messageLabel.setText("注册成功！请登录");
                messageLabel.setStyle("-fx-text-fill: #27AE60;");
            } else {
                messageLabel.setText("注册失败，手机号可能已存在");
                messageLabel.setStyle("-fx-text-fill: #E74C3C;");
            }
        });

        root.getChildren().addAll(titleLabel, loginForm);

        stage.setTitle("StardewBombers - 登录");
        stage.setScene(new Scene(root, 400, 300));
        stage.setResizable(false);
        stage.show();
    }
}
