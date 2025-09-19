package com.stardewbombers.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.stardewbombers.model.Player;
import com.stardewbombers.service.PlayerService;
import com.stardewbombers.service.impl.PlayerServiceImpl;

import java.util.function.Consumer;

public class LoginRegisterView {
    private final PlayerService service = new PlayerServiceImpl();
    private final Consumer<Player> onLoginSuccess;

    public LoginRegisterView(Consumer<Player> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public void show(Stage stage) {
        // 设置场景
        Scene scene = new Scene(new StackPane(), 500, 400);
        
        // 设置背景图片
        setBackgroundImage(scene);
        
        // 在背景之上添加内容
        addContentToBackground(scene);
        
        stage.setTitle("StardewBombers - 注册/登录");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    /**
     * 在背景之上添加内容
     */
    private void addContentToBackground(Scene scene) {
        // 获取背景容器
        StackPane backgroundContainer = (StackPane) scene.getRoot();
        
        // 创建登录注册内容
        VBox contentContainer = createContentContainer();
        
        // 将内容添加到背景容器之上
        backgroundContainer.getChildren().add(contentContainer);
    }
    
    /**
     * 设置场景背景图片
     */
    private void setBackgroundImage(Scene scene) {
        try {
            // 尝试加载背景图片
            java.io.InputStream imageStream = getClass().getResourceAsStream("/loginback/lb.jpg");
            if (imageStream != null) {
                Image backgroundImage = new Image(imageStream);
                
                // 检查图片是否有效
                if (backgroundImage.getWidth() > 0 && backgroundImage.getHeight() > 0) {
                    System.out.println("成功加载背景图片: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());
                    
                    // 创建背景图片
                    javafx.scene.image.ImageView backgroundView = new javafx.scene.image.ImageView(backgroundImage);
                    backgroundView.setFitWidth(500);
                    backgroundView.setFitHeight(400);
                    backgroundView.setPreserveRatio(false); // 拉伸填充整个窗口
                    
                    // 创建背景容器
                    StackPane backgroundContainer = new StackPane();
                    backgroundContainer.getChildren().add(backgroundView);
                    
                    // 设置场景根节点
                    scene.setRoot(backgroundContainer);
                    return;
                } else {
                    System.out.println("背景图片无效，使用像素艺术风格背景");
                }
            } else {
                System.out.println("未找到背景图片文件，使用像素艺术风格背景");
            }
        } catch (Exception e) {
            System.err.println("加载背景图片时出错，使用像素艺术风格背景: " + e.getMessage());
        }
        
        // 如果无法加载背景图片，使用像素艺术风格的渐变背景
        setPixelArtBackground(scene);
    }
    
    /**
     * 设置像素艺术风格的备用背景
     */
    private void setPixelArtBackground(Scene scene) {
        // 创建像素艺术风格的渐变背景
        VBox background = new VBox();
        background.setPrefSize(500, 400);
        background.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, " +
            "#F4E4BC 0%, #E8D5A3 15%, #D4A574 30%, #C19A6B 50%, #B8956A 70%, #A67C52 85%, #8B6F47 100%);"
        );
        
        // 添加纹理效果
        VBox texture = new VBox();
        texture.setPrefSize(500, 400);
        texture.setStyle(
            "-fx-background-color: radial-gradient(center 50% 50%, radius 80%, " +
            "rgba(139, 111, 71, 0.1) 0%, transparent 70%);"
        );
        
        // 创建背景容器
        StackPane backgroundContainer = new StackPane();
        backgroundContainer.getChildren().addAll(background, texture);
        
        // 设置场景根节点
        scene.setRoot(backgroundContainer);
    }
    
    /**
     * 创建内容容器
     */
    private VBox createContentContainer() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        
        // 标题
        Label titleLabel = new Label("StardewBombers");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#8B0000")); // 深红棕色，更加醒目
        titleLabel.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1); " +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.6), 1, 0, 0, 0);"
        );
        
        // 创建自定义的标签页容器
        VBox tabContainer = createCustomTabContainer();
        
        container.getChildren().addAll(titleLabel, tabContainer);
        return container;
    }
    
    /**
     * 创建自定义标签页容器
     */
    private VBox createCustomTabContainer() {
        VBox container = new VBox(0);
        container.setPrefSize(400, 300);
        container.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 6; " +
            "-fx-border-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 15, 0, 0, 3); " +
            "-fx-effect: dropshadow(gaussian, rgba(139, 69, 19, 0.3), 8, 0, 0, 1);"
        );
        
        // 创建标签页头部
        HBox tabHeader = createTabHeader();
        
        // 创建内容区域
        StackPane contentArea = new StackPane();
        contentArea.setPrefHeight(250);
        contentArea.setStyle("-fx-background-color: transparent;");
        
        // 创建注册表单
        VBox registerForm = createRegisterFormVBox();
        registerForm.setVisible(true);
        
        // 创建登录表单
        VBox loginForm = createLoginFormVBox();
        loginForm.setVisible(false);
        
        // 将表单添加到内容区域
        contentArea.getChildren().addAll(registerForm, loginForm);
        
        // 设置标签页切换
        setupTabSwitching(tabHeader, registerForm, loginForm);
        
        container.getChildren().addAll(tabHeader, contentArea);
        return container;
    }
    
    /**
     * 创建标签页头部
     */
    private HBox createTabHeader() {
        HBox header = new HBox(0);
        header.setPrefHeight(50);
        header.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.8); " + // 米色，与背景协调
            "-fx-background-radius: 20 20 0 0; " +
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 0 0 2 0;"
        );
        
        // 注册标签
        Button registerTab = new Button("注册");
        registerTab.setPrefWidth(200);
        registerTab.setPrefHeight(50);
        registerTab.setStyle(
            "-fx-background-color: rgba(139, 69, 19, 0.8); " + // 深棕色背景
            "-fx-text-fill: #F5F5DC; " + // 米色文字
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 20 0 0 0; " +
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 0 2 0 0; " +
            "-fx-border-radius: 20 0 0 0; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);"
        );
        
        // 登录标签
        Button loginTab = new Button("登录");
        loginTab.setPrefWidth(200);
        loginTab.setPrefHeight(50);
        loginTab.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.6); " + // 米色背景
            "-fx-text-fill: #8B4513; " + // 深棕色文字
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 0 20 0 0; " +
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 0 0 0 2; " +
            "-fx-border-radius: 0 20 0 0;"
        );
        
        header.getChildren().addAll(registerTab, loginTab);
        
        // 存储标签按钮引用
        registerTab.setUserData("register");
        loginTab.setUserData("login");
        
        return header;
    }
    
    /**
     * 设置标签页切换
     */
    private void setupTabSwitching(HBox tabHeader, VBox registerForm, VBox loginForm) {
        for (javafx.scene.Node node : tabHeader.getChildren()) {
            if (node instanceof Button) {
                Button tab = (Button) node;
                tab.setOnAction(e -> {
                    // 重置所有标签样式
                    for (javafx.scene.Node n : tabHeader.getChildren()) {
                        if (n instanceof Button) {
                            Button btn = (Button) n;
                            if (btn.getUserData().equals("register")) {
                                btn.setStyle(
                                    "-fx-background-color: rgba(255, 255, 255, 0.3); " +
                                    "-fx-text-fill: #2C3E50; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-background-radius: 15 0 0 0; " +
                                    "-fx-border-color: #8B6F47; " +
                                    "-fx-border-width: 0 1 0 0; " +
                                    "-fx-border-radius: 15 0 0 0;"
                                );
                            } else {
                                btn.setStyle(
                                    "-fx-background-color: rgba(255, 255, 255, 0.3); " +
                                    "-fx-text-fill: #2C3E50; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-background-radius: 0 15 0 0; " +
                                    "-fx-border-color: #8B6F47; " +
                                    "-fx-border-width: 0 0 0 1; " +
                                    "-fx-border-radius: 0 15 0 0;"
                                );
                            }
                        }
                    }
                    
                    // 设置当前标签样式
                    if (tab.getUserData().equals("register")) {
                        tab.setStyle(
                            "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                            "-fx-text-fill: #2C3E50; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 15 0 0 0; " +
                            "-fx-border-color: #8B6F47; " +
                            "-fx-border-width: 0 1 0 0; " +
                            "-fx-border-radius: 15 0 0 0;"
                        );
                        registerForm.setVisible(true);
                        loginForm.setVisible(false);
                    } else {
                        tab.setStyle(
                            "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                            "-fx-text-fill: #2C3E50; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 0 15 0 0; " +
                            "-fx-border-color: #8B6F47; " +
                            "-fx-border-width: 0 0 0 1; " +
                            "-fx-border-radius: 0 15 0 0;"
                        );
                        registerForm.setVisible(false);
                        loginForm.setVisible(true);
                    }
                });
            }
        }
    }
    
    /**
     * 创建注册表单VBox
     */
    private VBox createRegisterFormVBox() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        GridPane form = createRegisterForm();
        container.getChildren().add(form);
        
        return container;
    }
    
    /**
     * 创建登录表单VBox
     */
    private VBox createLoginFormVBox() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: transparent;");
        
        GridPane form = createLoginForm();
        container.getChildren().add(form);
        
        return container;
    }
    
    /**
     * 创建注册表单
     */
    private GridPane createRegisterForm() {
        GridPane reg = new GridPane();
        reg.setPadding(new Insets(20));
        reg.setHgap(10);
        reg.setVgap(10);
        reg.setAlignment(Pos.CENTER);
        reg.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: rgba(139, 111, 71, 0.3); " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10;"
        );
        
        TextField nickname = new TextField();
        nickname.setPromptText("请输入昵称");
        nickname.setPrefWidth(200);
        nickname.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.9); " + // 米色背景
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #8B4513; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );
        
        TextField phone = new TextField();
        phone.setPromptText("请输入手机号");
        phone.setPrefWidth(200);
        phone.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.9); " + // 米色背景
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #8B4513; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );
        
        PasswordField pass = new PasswordField();
        pass.setPromptText("请输入密码");
        pass.setPrefWidth(200);
        pass.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.9); " + // 米色背景
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #8B4513; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );
        
        Button btnReg = new Button("注册");
        btnReg.setPrefWidth(200);
        btnReg.setPrefHeight(35);
        btnReg.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #8B4513, #A0522D); " + // 棕色渐变
            "-fx-text-fill: #F5F5DC; " + // 米色文字
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #654321; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 0, 2);"
        );
        
        Label regMsg = new Label();
        regMsg.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
        
        Label nicknameLabel = new Label("昵称:");
        nicknameLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 1, 0, 0, 0);");
        reg.addRow(0, nicknameLabel, nickname);
        
        Label phoneLabel = new Label("手机号:");
        phoneLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 1, 0, 0, 0);");
        reg.addRow(1, phoneLabel, phone);
        
        Label passLabel = new Label("密码:");
        passLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 1, 0, 0, 0);");
        reg.addRow(2, passLabel, pass);
        reg.addRow(3, btnReg);
        reg.addRow(4, regMsg);
        
        btnReg.setOnAction(e -> {
            boolean ok = service.register(phone.getText().trim(), nickname.getText().trim(), pass.getText());
            if (ok) {
                regMsg.setText("注册成功！");
                regMsg.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
            } else {
                regMsg.setText("注册失败(手机号重复?)");
                regMsg.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            }
        });
        
        return reg;
    }
    
    /**
     * 创建登录表单
     */
    private GridPane createLoginForm() {
        GridPane log = new GridPane();
        log.setPadding(new Insets(20));
        log.setHgap(10);
        log.setVgap(10);
        log.setAlignment(Pos.CENTER);
        log.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.1); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: rgba(139, 111, 71, 0.3); " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10;"
        );
        
        TextField lphone = new TextField();
        lphone.setPromptText("请输入手机号");
        lphone.setPrefWidth(200);
        lphone.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.9); " + // 米色背景
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #8B4513; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );
        
        PasswordField lpass = new PasswordField();
        lpass.setPromptText("请输入密码");
        lpass.setPrefWidth(200);
        lpass.setStyle(
            "-fx-background-color: rgba(245, 245, 220, 0.9); " + // 米色背景
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #8B4513; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 0, 1);"
        );
        
        Button btnLog = new Button("登录");
        btnLog.setPrefWidth(200);
        btnLog.setPrefHeight(35);
        btnLog.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #A0522D, #8B4513); " + // 深棕色渐变
            "-fx-text-fill: #F5F5DC; " + // 米色文字
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #654321; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 0, 2);"
        );
        
        Label logMsg = new Label();
        logMsg.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
        
        Label lphoneLabel = new Label("手机号:");
        lphoneLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 1, 0, 0, 0);");
        log.addRow(0, lphoneLabel, lphone);
        
        Label lpassLabel = new Label("密码:");
        lpassLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 1, 0, 0, 0);");
        log.addRow(1, lpassLabel, lpass);
        log.addRow(2, btnLog);
        log.addRow(3, logMsg);
        
        btnLog.setOnAction(e -> {
            boolean ok = service.login(lphone.getText().trim(), lpass.getText());
            if (ok) {
                Player p = service.getProfile(lphone.getText().trim());
                logMsg.setText("欢迎, " + (p == null ? "玩家" : p.getNickname()));
                logMsg.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");
                if (p != null && onLoginSuccess != null) {
                    onLoginSuccess.accept(p);
                }
            } else {
                logMsg.setText("账号或密码错误");
                logMsg.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");
            }
        });
        
        return log;
    }
}
