package com.stardewbombers.client;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.util.GameConfig;

import java.util.List;

/**
 * UI管理器 - 统一管理游戏界面的所有UI组件
 * 提供现代化的游戏界面设计
 */
public class UIManager {
    
    // 游戏暂停回调接口
    public interface GamePauseCallback {
        void toggleGamePause();
    }
    
    private GamePauseCallback pauseCallback;
    
    // UI组件
    private Group root;
    private Rectangle backgroundGradient;
    private Rectangle[] playerInfoPanels;
    private Label[] playerInfoLabels;
    private ProgressBar[] healthBars;
    private Circle[] statusIndicators;
    private Label statusLabel;
    private Circle escButton;
    private Rectangle volumePanel;
    private javafx.scene.control.Slider musicSlider;
    private javafx.scene.control.Slider soundSlider;
    private Label musicLabel;
    private Label soundLabel;
    private boolean volumePanelVisible = false;
    private javafx.stage.Stage volumeStage;
    private javafx.scene.Scene volumeScene;
    
    // 用户名显示组件
    private Label usernameLabel;
    
    // 样式常量 - 现代化配色方案
    private static final Color SUCCESS_COLOR = Color.web("#00D4AA");  // 青绿色
    private static final Color DANGER_COLOR = Color.web("#FF6B6B");   // 珊瑚红
    private static final Color WARNING_COLOR = Color.web("#FFD93D");  // 金黄色
    private static final Color TEXT_COLOR = Color.web("#FFFFFF");     // 纯白
    
    // 尺寸常量
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 90;
    private static final int PANEL_SPACING = 15;
    private static final int CORNER_RADIUS = 12;
    
    public UIManager(Group root) {
        this.root = root;
        initializeUI();
    }
    
    /**
     * 设置游戏暂停回调
     */
    public void setGamePauseCallback(GamePauseCallback callback) {
        this.pauseCallback = callback;
    }
    
    /**
     * 初始化UI组件
     */
    private void initializeUI() {
        // 首先创建背景，确保它在最底层
        createBackgroundGradient();
        
        // 将背景移到最底层
        backgroundGradient.toBack();
        
        createUsernameDisplay();
        createPlayerInfoPanels();
        createEscButton();
        createVolumePanel();
        createStatusBar();
        
        // 添加整体动画效果
        addInitialAnimations();
    }
    
    /**
     * 添加初始动画效果
     */
    private void addInitialAnimations() {
        // 为玩家面板添加渐入动画
        for (int i = 0; i < playerInfoPanels.length; i++) {
            if (playerInfoPanels[i] != null) {
                playerInfoPanels[i].setOpacity(0);
                playerInfoPanels[i].setScaleX(0.8);
                playerInfoPanels[i].setScaleY(0.8);
                
                // 创建渐入动画
                javafx.animation.FadeTransition fadeTransition = new javafx.animation.FadeTransition(Duration.millis(800), playerInfoPanels[i]);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.setDelay(Duration.millis(i * 150)); // 错开动画时间
                
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(600), playerInfoPanels[i]);
                scaleTransition.setFromX(0.8);
                scaleTransition.setToX(1.0);
                scaleTransition.setFromY(0.8);
                scaleTransition.setToY(1.0);
                scaleTransition.setDelay(Duration.millis(i * 150));
                
                // 并行播放动画
                javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(fadeTransition, scaleTransition);
                parallelTransition.play();
            }
        }
        
        // 为ESC按钮添加渐入动画
        if (escButton != null) {
            escButton.setOpacity(0);
            escButton.setScaleX(0.5);
            escButton.setScaleY(0.5);
            
            javafx.animation.FadeTransition escFade = new javafx.animation.FadeTransition(Duration.millis(1000), escButton);
            escFade.setFromValue(0);
            escFade.setToValue(1);
            escFade.setDelay(Duration.millis(600));
            
            ScaleTransition escScale = new ScaleTransition(Duration.millis(800), escButton);
            escScale.setFromX(0.5);
            escScale.setToX(1.0);
            escScale.setFromY(0.5);
            escScale.setToY(1.0);
            escScale.setDelay(Duration.millis(600));
            
            javafx.animation.ParallelTransition escParallel = new javafx.animation.ParallelTransition(escFade, escScale);
            escParallel.play();
        }
        
        // 为状态栏添加渐入动画
        if (statusLabel != null) {
            statusLabel.setOpacity(0);
            statusLabel.setTranslateY(20);
            
            javafx.animation.FadeTransition statusFade = new javafx.animation.FadeTransition(Duration.millis(800), statusLabel);
            statusFade.setFromValue(0);
            statusFade.setToValue(1);
            statusFade.setDelay(Duration.millis(1000));
            
            javafx.animation.TranslateTransition statusTranslate = new javafx.animation.TranslateTransition(Duration.millis(600), statusLabel);
            statusTranslate.setFromY(20);
            statusTranslate.setToY(0);
            statusTranslate.setDelay(Duration.millis(1000));
            
            javafx.animation.ParallelTransition statusParallel = new javafx.animation.ParallelTransition(statusFade, statusTranslate);
            statusParallel.play();
        }
    }
    
    /**
     * 创建背景渐变 - 覆盖整个窗口
     */
    private void createBackgroundGradient() {
        // 创建覆盖整个窗口的背景
        backgroundGradient = new Rectangle(900, 600); // 覆盖整个窗口
        backgroundGradient.setX(0); // 从窗口左边开始
        backgroundGradient.setY(0);
        
        // 创建更丰富的渐变效果 - 深空主题
        LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, Color.web("#0F0F23")),      // 深紫蓝
            new Stop(0.2, Color.web("#1A1A2E")),    // 深蓝紫
            new Stop(0.4, Color.web("#16213E")),    // 中蓝
            new Stop(0.6, Color.web("#0F3460")),    // 深蓝
            new Stop(0.8, Color.web("#0A2647")),    // 更深蓝
            new Stop(1, Color.web("#051A2E"))       // 最深蓝
        );
        
        backgroundGradient.setFill(gradient);
        root.getChildren().add(backgroundGradient);
        
        // 添加星空效果背景（覆盖整个窗口）
        createStarField();
    }
    
    /**
     * 创建星空效果 - 覆盖整个窗口
     */
    private void createStarField() {
        for (int i = 0; i < 50; i++) { // 增加星星数量，覆盖整个窗口
            Circle star = new Circle(
                Math.random() * 900, // 覆盖整个窗口宽度（0-900）
                Math.random() * 600, // 覆盖整个窗口高度（0-600）
                Math.random() * 2 + 0.5
            );
            star.setFill(Color.WHITE);
            star.setOpacity(Math.random() * 0.8 + 0.2);
            root.getChildren().add(star);
        }
    }
    
    /**
     * 创建用户名显示 - 左上角黑色像素风格
     */
    private void createUsernameDisplay() {
        // 创建用户名标签
        usernameLabel = new Label("Hi，玩家");
        usernameLabel.setLayoutX(30);
        usernameLabel.setLayoutY(30);
        
        // 设置像素风格字体 - 使用等宽字体模拟像素效果
        Font pixelFont = Font.font("Courier New", FontWeight.BOLD, 18);
        usernameLabel.setFont(pixelFont);
        
        // 设置黑色文字
        usernameLabel.setTextFill(Color.BLACK);
        
        // 添加像素风格的描边效果
        DropShadow pixelShadow = new DropShadow();
        pixelShadow.setRadius(0);
        pixelShadow.setColor(Color.WHITE);
        pixelShadow.setOffsetX(2);
        pixelShadow.setOffsetY(2);
        usernameLabel.setEffect(pixelShadow);
        
        // 添加背景框 - 像素风格
        Rectangle usernameBg = new Rectangle(200, 40);
        usernameBg.setX(20);
        usernameBg.setY(20);
        usernameBg.setFill(Color.color(1, 1, 1, 0.9)); // 更不透明的白色背景
        usernameBg.setStroke(Color.BLACK);
        usernameBg.setStrokeWidth(3);
        usernameBg.setArcWidth(0); // 直角边框，像素风格
        usernameBg.setArcHeight(0);
        
        // 将背景和标签添加到根节点，确保在最前面
        root.getChildren().add(usernameBg);
        root.getChildren().add(usernameLabel);
        
        // 确保用户名显示在最前面
        usernameBg.toFront();
        usernameLabel.toFront();
        
        System.out.println("用户名显示组件已创建并添加到UI");
    }
    
    /**
     * 创建玩家信息面板 - 垂直一列布局（只显示3个玩家）
     */
    private void createPlayerInfoPanels() {
        playerInfoPanels = new Rectangle[3];
        playerInfoLabels = new Label[3];
        healthBars = new ProgressBar[3];
        statusIndicators = new Circle[3];
        
        int startX = 620; // 地图右边
        int startY = 20;
        
        for (int i = 0; i < 3; i++) {
            createPlayerPanel(i, startX, startY + i * (PANEL_HEIGHT + PANEL_SPACING));
        }
    }
    
    /**
     * 创建单个玩家面板 - 玻璃拟态设计
     */
    private void createPlayerPanel(int index, int x, int y) {
        // 主面板背景 - 玻璃拟态效果
        Rectangle panel = new Rectangle(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setX(x);
        panel.setY(y);
        
        // 创建玻璃拟态渐变
        LinearGradient glassGradient = new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, Color.color(1, 1, 1, 0.25)),    // 半透明白色
            new Stop(0.3, Color.color(1, 1, 1, 0.1)),   // 更透明
            new Stop(0.7, Color.color(0, 0, 0, 0.1)),   // 半透明黑色
            new Stop(1, Color.color(0, 0, 0, 0.2))      // 更深半透明
        );
        panel.setFill(glassGradient);
        
        // 玻璃边框效果
        panel.setStroke(Color.color(1, 1, 1, 0.3));
        panel.setStrokeWidth(1.5);
        panel.setArcWidth(CORNER_RADIUS);
        panel.setArcHeight(CORNER_RADIUS);
        
        // 添加玻璃阴影效果
        DropShadow glassShadow = new DropShadow();
        glassShadow.setRadius(15);
        glassShadow.setColor(Color.color(0, 0, 0, 0.3));
        glassShadow.setOffsetX(0);
        glassShadow.setOffsetY(8);
        panel.setEffect(glassShadow);
        
        playerInfoPanels[index] = panel;
        root.getChildren().add(panel);
        
        // 添加悬停效果
        addGlassHoverEffect(panel);
        
        // 状态指示器（圆形）- 添加发光效果
        Circle statusIndicator = new Circle(x + 15, y + 15, 7);
        statusIndicator.setFill(DANGER_COLOR);
        statusIndicator.setStroke(Color.WHITE);
        statusIndicator.setStrokeWidth(2);
        
        // 添加发光效果 - 简化版本
        DropShadow glow = new DropShadow();
        glow.setRadius(4);
        glow.setColor(DANGER_COLOR);
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        statusIndicator.setEffect(glow);
        
        statusIndicators[index] = statusIndicator;
        root.getChildren().add(statusIndicator);
        
        // 玩家信息标签 - 优化字体
        Label label = new Label();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setLayoutX(x + 25);
        label.setLayoutY(y + 8);
        label.setTextFill(TEXT_COLOR);
        label.setWrapText(true);
        label.setPrefWidth(PANEL_WIDTH - 30);
        
        // 添加文字阴影
        DropShadow textShadow = new DropShadow();
        textShadow.setRadius(2);
        textShadow.setColor(Color.color(0, 0, 0, 0.5));
        textShadow.setOffsetX(1);
        textShadow.setOffsetY(1);
        label.setEffect(textShadow);
        
        playerInfoLabels[index] = label;
        root.getChildren().add(label);
        
        // 生命值标签 - 优化样式
        Label healthLabel = new Label("❤️ 生命值");
        healthLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 10));
        healthLabel.setLayoutX(x + 12);
        healthLabel.setLayoutY(y + 30);
        healthLabel.setTextFill(Color.web("#FF6B6B"));
        root.getChildren().add(healthLabel);
        
        // 生命值进度条 - 美化样式
        ProgressBar healthBar = new ProgressBar(1.0);
        healthBar.setLayoutX(x + 12);
        healthBar.setLayoutY(y + 42);
        healthBar.setPrefWidth(PANEL_WIDTH - 24);
        healthBar.setPrefHeight(10);
        healthBar.setStyle(
            "-fx-accent: #00D4AA;" +
            "-fx-control-inner-background: #1A202C;" +
            "-fx-background-color: #2D3748;" +
            "-fx-border-color: #4A5568;" +
            "-fx-border-width: 1px;"
        );
        healthBars[index] = healthBar;
        root.getChildren().add(healthBar);
        
        // 威力信息 - 优化样式
        Label powerLabel = new Label("⚡ 威力: 2");
        powerLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 10));
        powerLabel.setLayoutX(x + 12);
        powerLabel.setLayoutY(y + 54);
        powerLabel.setTextFill(Color.web("#FFD93D"));
        root.getChildren().add(powerLabel);
        
        // 玩家状态 - 优化样式
        Label statusLabel = new Label("🟢 状态: 存活");
        statusLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 10));
        statusLabel.setLayoutX(x + 12);
        statusLabel.setLayoutY(y + 74);
        statusLabel.setTextFill(SUCCESS_COLOR);
        root.getChildren().add(statusLabel);
    }
    
    /**
     * 创建Esc按钮 - 炫酷设计
     */
    private void createEscButton() {
        // 计算Esc按钮位置（在玩家面板下方）
        int escButtonX = 620 + PANEL_WIDTH / 2 - 30; // 居中
        int escButtonY = 20 + 3 * (PANEL_HEIGHT + PANEL_SPACING) + 35;
        
        // 创建外圈发光效果
        Circle outerGlow = new Circle(escButtonX, escButtonY, 35);
        RadialGradient glowGradient = new RadialGradient(
            0, 0, 0.5, 0.5, 1, true, null,
            new Stop(0, Color.color(0.26, 0.61, 0.88, 0.3)),  // 半透明蓝
            new Stop(0.7, Color.color(0.26, 0.61, 0.88, 0.1)), // 更透明
            new Stop(1, Color.TRANSPARENT)                      // 完全透明
        );
        outerGlow.setFill(glowGradient);
        root.getChildren().add(outerGlow);
        
        // 创建主按钮圆形
        escButton = new Circle(escButtonX, escButtonY, 28);
        
        // 创建炫酷按钮渐变
        RadialGradient buttonGradient = new RadialGradient(
            0, 0, 0.5, 0.5, 1, true, null,
            new Stop(0, Color.web("#64B5F6")),      // 亮蓝
            new Stop(0.3, Color.web("#42A5F5")),    // 中亮蓝
            new Stop(0.7, Color.web("#2196F3")),    // 中蓝
            new Stop(1, Color.web("#1976D2"))       // 深蓝
        );
        escButton.setFill(buttonGradient);
        
        // 创建渐变边框
        RadialGradient borderGradient = new RadialGradient(
            0, 0, 0.5, 0.5, 1, true, null,
            new Stop(0, Color.web("#90CAF9")),      // 浅蓝
            new Stop(1, Color.web("#1565C0"))       // 深蓝
        );
        escButton.setStroke(borderGradient);
        escButton.setStrokeWidth(2.5);
        
        // 添加多层阴影效果
        DropShadow innerShadow = new DropShadow();
        innerShadow.setRadius(8);
        innerShadow.setColor(Color.color(0, 0, 0, 0.3));
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(2);
        
        DropShadow outerShadow = new DropShadow();
        outerShadow.setRadius(15);
        outerShadow.setColor(Color.color(0.26, 0.61, 0.88, 0.4));
        outerShadow.setOffsetX(0);
        outerShadow.setOffsetY(4);
        
        // 组合阴影效果
        innerShadow.setInput(outerShadow);
        escButton.setEffect(innerShadow);
        
        // 添加悬停效果
        escButton.setOnMouseEntered(event -> {
            // 悬停时改变颜色
            RadialGradient hoverGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1, true, null,
                new Stop(0, Color.web("#81C784")),      // 亮绿
                new Stop(0.3, Color.web("#66BB6A")),    // 中亮绿
                new Stop(0.7, Color.web("#4CAF50")),    // 中绿
                new Stop(1, Color.web("#388E3C"))       // 深绿
            );
            escButton.setFill(hoverGradient);
            
            // 缩放效果
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), escButton);
            scaleTransition.setToX(1.15);
            scaleTransition.setToY(1.15);
            scaleTransition.play();
            
            // 外圈发光效果增强
            ScaleTransition glowTransition = new ScaleTransition(Duration.millis(200), outerGlow);
            glowTransition.setToX(1.2);
            glowTransition.setToY(1.2);
            glowTransition.play();
        });
        
        escButton.setOnMouseExited(event -> {
            // 恢复原始颜色
            escButton.setFill(buttonGradient);
            
            // 恢复原始大小
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), escButton);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            // 恢复外圈大小
            ScaleTransition glowTransition = new ScaleTransition(Duration.millis(200), outerGlow);
            glowTransition.setToX(1.0);
            glowTransition.setToY(1.0);
            glowTransition.play();
        });
        
        // 添加点击事件
        escButton.setOnMouseClicked(event -> {
            // 点击时的脉冲效果
            ScaleTransition pulseTransition = new ScaleTransition(Duration.millis(100), escButton);
            pulseTransition.setToX(0.9);
            pulseTransition.setToY(0.9);
            pulseTransition.setAutoReverse(true);
            pulseTransition.setCycleCount(2);
            pulseTransition.play();
            
            // 切换音量面板显示状态
            toggleVolumePanel();
        });
        
        root.getChildren().add(escButton);
        
        // 添加ESC文字标签 - 优化样式
        Label escLabel = new Label("ESC");
        escLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        escLabel.setLayoutX(escButtonX - 20);
        escLabel.setLayoutY(escButtonY - 12);
        escLabel.setTextFill(Color.WHITE);
        
        // 添加文字发光效果
        DropShadow textGlow = new DropShadow();
        textGlow.setRadius(4);
        textGlow.setColor(Color.color(0, 0, 0, 0.8));
        textGlow.setOffsetX(0);
        textGlow.setOffsetY(0);
        escLabel.setEffect(textGlow);
        
        root.getChildren().add(escLabel);
    }
    
    /**
     * 创建音量调节面板（弹窗形式）- 优化设计
     */
    private void createVolumePanel() {
        // 创建弹窗内容
        Group volumeGroup = new Group();
        
        // 音量面板背景 - 玻璃拟态设计
        volumePanel = new Rectangle(320, 220);
        volumePanel.setX(0);
        volumePanel.setY(0);
        
        // 创建更不透明的背景渐变
        LinearGradient glassGradient = new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, Color.color(1, 1, 1, 0.8)),      // 更不透明的白色
            new Stop(0.3, Color.color(1, 1, 1, 0.6)),   // 中等透明度
            new Stop(0.7, Color.color(0, 0, 0, 0.4)),   // 半透明黑色
            new Stop(1, Color.color(0, 0, 0, 0.6))       // 更不透明的黑色
        );
        volumePanel.setFill(glassGradient);
        
        // 更明显的边框
        volumePanel.setStroke(Color.color(1, 1, 1, 0.8));
        volumePanel.setStrokeWidth(2);
        volumePanel.setArcWidth(CORNER_RADIUS);
        volumePanel.setArcHeight(CORNER_RADIUS);
        
        // 添加更明显的阴影效果
        DropShadow glassShadow = new DropShadow();
        glassShadow.setRadius(25);
        glassShadow.setColor(Color.color(0, 0, 0, 0.7));
        glassShadow.setOffsetX(0);
        glassShadow.setOffsetY(12);
        volumePanel.setEffect(glassShadow);
        
        volumeGroup.getChildren().add(volumePanel);
        
        // 音量面板标题 - 优化样式
        Label titleLabel = new Label("🎵 音量设置");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(20);
        titleLabel.setTextFill(TEXT_COLOR);
        
        // 添加标题发光效果
        DropShadow titleGlow = new DropShadow();
        titleGlow.setRadius(4);
        titleGlow.setColor(Color.color(0.26, 0.61, 0.88, 0.6));
        titleGlow.setOffsetX(0);
        titleGlow.setOffsetY(0);
        titleLabel.setEffect(titleGlow);
        
        volumeGroup.getChildren().add(titleLabel);
        
        // 音乐音量标签 - 显示当前音量
        int currentMusicVolume = (int)(MusicManager.getMusicVolume() * 100);
        musicLabel = new Label("🎵 音乐音量: " + currentMusicVolume + "%");
        musicLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        musicLabel.setLayoutX(20);
        musicLabel.setLayoutY(60);
        musicLabel.setTextFill(TEXT_COLOR);
        
        // 添加标签发光效果
        DropShadow labelGlow = new DropShadow();
        labelGlow.setRadius(2);
        labelGlow.setColor(Color.color(0.26, 0.61, 0.88, 0.4));
        labelGlow.setOffsetX(0);
        labelGlow.setOffsetY(0);
        musicLabel.setEffect(labelGlow);
        
        volumeGroup.getChildren().add(musicLabel);
        
        // 音乐音量滑块 - 优化样式
        musicSlider = new Slider(0, 100, currentMusicVolume);
        musicSlider.setLayoutX(20);
        musicSlider.setLayoutY(85);
        musicSlider.setPrefWidth(280);
        musicSlider.setPrefHeight(25);
        musicSlider.setStyle(
            "-fx-control-inner-background: #1A202C;" +
            "-fx-control-inner-background-alt: #2D3748;" +
            "-fx-background-color: #4A5568;" +
            "-fx-border-color: #4299E1;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );
        volumeGroup.getChildren().add(musicSlider);
        
        // 音效音量标签 - 显示当前音量
        int currentSoundVolume = (int)(MusicManager.getSoundVolume() * 100);
        soundLabel = new Label("🔊 音效音量: " + currentSoundVolume + "%");
        soundLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        soundLabel.setLayoutX(20);
        soundLabel.setLayoutY(130);
        soundLabel.setTextFill(TEXT_COLOR);
        
        // 添加标签发光效果
        DropShadow soundLabelGlow = new DropShadow();
        soundLabelGlow.setRadius(2);
        soundLabelGlow.setColor(Color.color(0.26, 0.61, 0.88, 0.4));
        soundLabelGlow.setOffsetX(0);
        soundLabelGlow.setOffsetY(0);
        soundLabel.setEffect(soundLabelGlow);
        
        volumeGroup.getChildren().add(soundLabel);
        
        // 音效音量滑块 - 优化样式
        soundSlider = new Slider(0, 100, currentSoundVolume);
        soundSlider.setLayoutX(20);
        soundSlider.setLayoutY(155);
        soundSlider.setPrefWidth(280);
        soundSlider.setPrefHeight(25);
        soundSlider.setStyle(
            "-fx-control-inner-background: #1A202C;" +
            "-fx-control-inner-background-alt: #2D3748;" +
            "-fx-background-color: #4A5568;" +
            "-fx-border-color: #4299E1;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;"
        );
        volumeGroup.getChildren().add(soundSlider);
        
        // 关闭按钮 - 优化样式
        Label closeButton = new Label("✕ 关闭");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        closeButton.setLayoutX(270);
        closeButton.setLayoutY(20);
        closeButton.setTextFill(Color.web("#FF6B6B"));
        
        // 添加关闭按钮发光效果
        DropShadow closeGlow = new DropShadow();
        closeGlow.setRadius(3);
        closeGlow.setColor(Color.color(1, 0.42, 0.42, 0.6));
        closeGlow.setOffsetX(0);
        closeGlow.setOffsetY(0);
        closeButton.setEffect(closeGlow);
        
        // 关闭按钮悬停效果
        closeButton.setOnMouseEntered(event -> {
            closeButton.setTextFill(Color.web("#FF4757"));
            closeButton.setScaleX(1.1);
            closeButton.setScaleY(1.1);
        });
        closeButton.setOnMouseExited(event -> {
            closeButton.setTextFill(Color.web("#FF6B6B"));
            closeButton.setScaleX(1.0);
            closeButton.setScaleY(1.0);
        });
        closeButton.setOnMouseClicked(event -> {
            closeVolumePanel();
        });
        volumeGroup.getChildren().add(closeButton);
        
        // 添加滑块事件监听
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int volume = newVal.intValue();
            musicLabel.setText("🎵 音乐音量: " + volume + "%");
            // 调用MusicManager设置音乐音量 (0.0-1.0)
            MusicManager.setMusicVolume(volume / 100.0);
        });
        
        soundSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int volume = newVal.intValue();
            soundLabel.setText("🔊 音效音量: " + volume + "%");
            // 调用MusicManager设置音效音量 (0.0-1.0)
            MusicManager.setSoundVolume(volume / 100.0);
        });
        
        // 创建弹窗场景 - 更新尺寸
        volumeScene = new Scene(volumeGroup, 320, 220);
        volumeScene.setFill(Color.TRANSPARENT);
        
        // 创建弹窗舞台
        volumeStage = new Stage();
        volumeStage.setScene(volumeScene);
        volumeStage.setTitle("音量设置");
        volumeStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        volumeStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        volumeStage.setResizable(false);
        volumeStage.setAlwaysOnTop(true);
        
        // 初始隐藏
        volumeStage.hide();
    }
    
    /**
     * 切换音量面板显示状态
     */
    private void toggleVolumePanel() {
        if (volumePanelVisible) {
            closeVolumePanel();
        } else {
            showVolumePanel();
            // 显示音量窗口时暂停游戏
            if (pauseCallback != null) {
                pauseCallback.toggleGamePause();
            }
        }
    }
    
    /**
     * 显示音量面板
     */
    private void showVolumePanel() {
        volumePanelVisible = true;
        // 居中显示弹窗
        volumeStage.setX(400); // 可以根据主窗口位置调整
        volumeStage.setY(200);
        volumeStage.show();
    }
    
    /**
     * 关闭音量面板
     */
    private void closeVolumePanel() {
        volumePanelVisible = false;
        volumeStage.hide();
        
        // 关闭音量窗口时恢复游戏运行状态
        if (pauseCallback != null) {
            pauseCallback.toggleGamePause();
        }
    }
    
    
    /**
     * 创建状态栏 - 动态效果设计
     */
    private void createStatusBar() {
        // 状态标签 - 优化样式
        statusLabel = new Label("🎮 游戏状态: 运行中");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setLayoutX(15);
        statusLabel.setLayoutY(570);
        statusLabel.setTextFill(TEXT_COLOR);
        
        // 添加动态文字发光效果
        DropShadow textGlow = new DropShadow();
        textGlow.setRadius(3);
        textGlow.setColor(Color.color(0.26, 0.61, 0.88, 0.6));
        textGlow.setOffsetX(0);
        textGlow.setOffsetY(0);
        statusLabel.setEffect(textGlow);
        
        root.getChildren().add(statusLabel);
        
        // 状态栏背景 - 玻璃拟态设计
        Rectangle statusBarBg = new Rectangle(276, 30);
        statusBarBg.setX(8);
        statusBarBg.setY(563);
        
        // 创建玻璃拟态渐变
        LinearGradient glassGradient = new LinearGradient(
            0, 0, 0, 1, true, null,
            new Stop(0, Color.color(1, 1, 1, 0.2)),     // 半透明白色
            new Stop(0.5, Color.color(1, 1, 1, 0.1)),   // 更透明
            new Stop(1, Color.color(0, 0, 0, 0.15))     // 半透明黑色
        );
        statusBarBg.setFill(glassGradient);
        
        // 玻璃边框
        statusBarBg.setStroke(Color.color(1, 1, 1, 0.3));
        statusBarBg.setStrokeWidth(1.5);
        statusBarBg.setArcWidth(12);
        statusBarBg.setArcHeight(12);
        
        // 添加玻璃阴影效果
        DropShadow glassShadow = new DropShadow();
        glassShadow.setRadius(10);
        glassShadow.setColor(Color.color(0, 0, 0, 0.4));
        glassShadow.setOffsetX(0);
        glassShadow.setOffsetY(4);
        statusBarBg.setEffect(glassShadow);
        
        root.getChildren().add(statusBarBg);
        
        // 添加动态边框发光效果 - 延长边框
        Rectangle statusBorder = new Rectangle(280, 34);
        statusBorder.setX(6);
        statusBorder.setY(561);
        statusBorder.setFill(Color.TRANSPARENT);
        
        // 创建动态边框渐变
        LinearGradient borderGradient = new LinearGradient(
            0, 0, 1, 0, true, null,
            new Stop(0, Color.TRANSPARENT),
            new Stop(0.2, Color.web("#4299E1")),
            new Stop(0.4, Color.web("#00D4AA")),
            new Stop(0.6, Color.web("#FFD93D")),
            new Stop(0.8, Color.web("#FF6B6B")),
            new Stop(1, Color.TRANSPARENT)
        );
        statusBorder.setStroke(borderGradient);
        statusBorder.setStrokeWidth(2);
        statusBorder.setArcWidth(14);
        statusBorder.setArcHeight(14);
        
        // 添加边框发光效果
        DropShadow borderGlow = new DropShadow();
        borderGlow.setRadius(8);
        borderGlow.setColor(Color.color(0.26, 0.61, 0.88, 0.5));
        borderGlow.setOffsetX(0);
        borderGlow.setOffsetY(0);
        statusBorder.setEffect(borderGlow);
        
        root.getChildren().add(statusBorder);
        
        // 将背景移到文字后面
        statusBarBg.toBack();
        statusBorder.toBack();
    }
    
    
    /**
     * 更新玩家信息（只显示3个玩家）
     */
    public void updatePlayerInfo(List<PlayerComponent> players) {
        for (int i = 0; i < 3; i++) {
            if (i < players.size()) {
                PlayerComponent playerComponent = players.get(i);
                Player player = playerComponent.getPlayer();
                
                // 更新状态指示器
                if (player.isAlive()) {
                    statusIndicators[i].setFill(SUCCESS_COLOR);
                } else {
                    statusIndicators[i].setFill(DANGER_COLOR);
                }
                
                // 更新生命值进度条
                double healthPercent = (double) player.getHealth() / GameConfig.PLAYER_MAX_HP;
                healthBars[i].setProgress(healthPercent);
                
                // 更新生命值颜色
                if (healthPercent > 0.6) {
                    healthBars[i].setStyle("-fx-accent: " + toHexColor(SUCCESS_COLOR) + ";");
                } else if (healthPercent > 0.3) {
                    healthBars[i].setStyle("-fx-accent: " + toHexColor(WARNING_COLOR) + ";");
                } else {
                    healthBars[i].setStyle("-fx-accent: " + toHexColor(DANGER_COLOR) + ";");
                }
                
                // 更新玩家信息标签
                String playerInfo = String.format("玩家%d (%s)", i + 1, player.getId());
                playerInfoLabels[i].setText(playerInfo);
                
            } else {
                // 未连接的玩家
                statusIndicators[i].setFill(Color.GRAY);
                healthBars[i].setProgress(0);
                playerInfoLabels[i].setText(String.format("玩家%d (未连接)", i + 1));
            }
        }
    }
    
    /**
     * 更新状态栏
     */
    public void updateStatusBar(boolean gameRunning, String mapName) {
        StringBuilder statusText = new StringBuilder();
        statusText.append(String.format("游戏状态: %s | 地图: %s", 
            gameRunning ? "运行中" : "暂停", mapName));
        
        statusLabel.setText(statusText.toString());
    }
    
    
    
    /**
     * 添加玻璃悬停效果
     */
    private void addGlassHoverEffect(Rectangle panel) {
        panel.setOnMouseEntered(event -> {
            // 悬停时增加透明度
            LinearGradient hoverGradient = new LinearGradient(
                0, 0, 0, 1, true, null,
                new Stop(0, Color.color(1, 1, 1, 0.4)),    // 更亮的半透明白色
                new Stop(0.3, Color.color(1, 1, 1, 0.2)),   // 更透明
                new Stop(0.7, Color.color(0, 0, 0, 0.15)),  // 半透明黑色
                new Stop(1, Color.color(0, 0, 0, 0.3))      // 更深半透明
            );
            panel.setFill(hoverGradient);
            
            // 轻微缩放效果
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), panel);
            scaleTransition.setToX(1.02);
            scaleTransition.setToY(1.02);
            scaleTransition.play();
        });
        
        panel.setOnMouseExited(event -> {
            // 恢复原始透明度
            LinearGradient normalGradient = new LinearGradient(
                0, 0, 0, 1, true, null,
                new Stop(0, Color.color(1, 1, 1, 0.25)),    // 半透明白色
                new Stop(0.3, Color.color(1, 1, 1, 0.1)),   // 更透明
                new Stop(0.7, Color.color(0, 0, 0, 0.1)),   // 半透明黑色
                new Stop(1, Color.color(0, 0, 0, 0.2))      // 更深半透明
            );
            panel.setFill(normalGradient);
            
            // 恢复原始大小
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), panel);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    /**
     * 添加悬停效果
     */
    public void addHoverEffect(Rectangle panel, int index) {
        panel.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), panel);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        
        panel.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), panel);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    /**
     * 颜色转十六进制字符串
     */
    private String toHexColor(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    /**
     * 更新用户名显示
     */
    public void updateUsername(String nickname) {
        System.out.println("UIManager.updateUsername被调用，昵称: " + nickname);
        if (usernameLabel != null) {
            usernameLabel.setText("Hi，" + nickname);
            System.out.println("用户名标签已更新为: Hi，" + nickname);
        } else {
            System.out.println("用户名标签为null，无法更新");
        }
    }
    
    /**
     * 获取所有UI组件
     */
    public Group getRoot() {
        return root;
    }
}
