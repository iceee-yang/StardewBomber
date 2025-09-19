package com.stardewbombers.client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import com.stardewbombers.model.Player;

/**
 * 主菜单界面
 * 使用stardewbackground.png作为背景，包含开始游戏和退出游戏按钮
 */
public class MainMenuView {
    
    private Stage primaryStage;
    private Scene menuScene;
    private VBox buttonContainer;
    private Button startGameButton;
    private Button exitGameButton;
    private Button farmButton;
    private String selectedMap; // 存储选择的地图
    private Player currentPlayer; // 当前登录的用户
    private javafx.scene.control.Label usernameLabel; // 用户名显示标签
    
    // 界面尺寸
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    // 按钮尺寸（备用）
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    
    // 开始游戏按钮尺寸
    private static final int START_BUTTON_WIDTH = 300;
    private static final int START_BUTTON_HEIGHT = 90;
    
    // 退出游戏按钮尺寸
    private static final int EXIT_BUTTON_WIDTH = 250;
    private static final int EXIT_BUTTON_HEIGHT = 75;
    
    // 农场按钮尺寸
    private static final int FARM_BUTTON_WIDTH = 120;
    private static final int FARM_BUTTON_HEIGHT = 120;
    
    public MainMenuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.currentPlayer = null;
        createMainMenu();
    }
    
    public MainMenuView(Stage primaryStage, Player player) {
        this.primaryStage = primaryStage;
        this.currentPlayer = player;
        System.out.println("MainMenuView创建，用户信息: " + (player != null ? player.getNickname() : "null"));
        createMainMenu();
    }
    
    /**
     * 创建主菜单界面
     */
    private void createMainMenu() {
        // 创建根容器
        Group root = new Group();
        
        // 创建背景
        createBackground(root);
        
        // 创建按钮容器
        createButtonContainer(root);
        
        // 创建用户名显示
        createUsernameDisplay(root);
        
        // 创建场景
        menuScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // 设置场景背景色（作为备用）
        menuScene.setFill(Color.web("#2C3E50"));
    }
    
    /**
     * 创建背景
     */
    private void createBackground(Group root) {
        try {
            // 加载背景图片
            Image backgroundImage = new Image(getClass().getResourceAsStream("/mainview/stardewbackground.png"));
            ImageView backgroundView = new ImageView(backgroundImage);
            
            // 设置背景图片尺寸，保持宽高比
            double imageWidth = backgroundImage.getWidth();
            double imageHeight = backgroundImage.getHeight();
            double scaleX = WINDOW_WIDTH / imageWidth;
            double scaleY = WINDOW_HEIGHT / imageHeight;
            double scale = Math.max(scaleX, scaleY); // 使用较大的缩放比例确保完全覆盖
            
            backgroundView.setFitWidth(imageWidth * scale);
            backgroundView.setFitHeight(imageHeight * scale);
            
            // 居中显示
            backgroundView.setX((WINDOW_WIDTH - backgroundView.getFitWidth()) / 2);
            backgroundView.setY((WINDOW_HEIGHT - backgroundView.getFitHeight()) / 2);
            
            // 将背景放在最底层
            backgroundView.toBack();
            root.getChildren().add(backgroundView);
            
        } catch (Exception e) {
            System.err.println("无法加载背景图片: " + e.getMessage());
            // 如果无法加载背景图片，使用纯色背景
            javafx.scene.shape.Rectangle fallbackBackground = new javafx.scene.shape.Rectangle(
                WINDOW_WIDTH, WINDOW_HEIGHT, Color.web("#2C3E50"));
            fallbackBackground.toBack();
            root.getChildren().add(fallbackBackground);
        }
    }
    
    /**
     * 创建按钮容器
     */
    private void createButtonContainer(Group root) {
        // 创建垂直布局容器，减少间距让退出按钮上移
        buttonContainer = new VBox(20); // 减少间距，让退出按钮上移一点点
        buttonContainer.setAlignment(Pos.CENTER);
        
        // 设置容器位置（以开始按钮宽度为准居中）
        buttonContainer.setLayoutX((WINDOW_WIDTH - START_BUTTON_WIDTH) / 2);
        buttonContainer.setLayoutY(WINDOW_HEIGHT * 0.7); // 保持当前位置
        
        // 创建开始游戏按钮
        createStartGameButton();
        
        // 创建退出游戏按钮
        createExitGameButton();
        
        // 创建农场按钮
        createFarmButton();
        
        // 添加按钮到容器
        buttonContainer.getChildren().addAll(startGameButton, exitGameButton);
        
        // 将农场按钮单独添加到根节点，放在右下角
        farmButton.setLayoutX(WINDOW_WIDTH - FARM_BUTTON_WIDTH - 50); // 距离右边50像素（往左移动30px）
        farmButton.setLayoutY(WINDOW_HEIGHT - FARM_BUTTON_HEIGHT - 50); // 距离底部50像素（往上移动30px）
        root.getChildren().add(farmButton);
        
        // 添加容器到根节点
        root.getChildren().add(buttonContainer);
        
        // 添加初始动画
        addInitialAnimation();
    }
    
    /**
     * 创建开始游戏按钮
     */
    private void createStartGameButton() {
        try {
            // 加载开始游戏按钮图片
            Image startImage = new Image(getClass().getResourceAsStream("/mainview/startgame.png"));
            ImageView startImageView = new ImageView(startImage);
            
            // 设置按钮尺寸 - 开始游戏按钮更大
            startImageView.setFitWidth(START_BUTTON_WIDTH);
            startImageView.setFitHeight(START_BUTTON_HEIGHT);
            startImageView.setPreserveRatio(true); // 保持宽高比，按比例缩放
            
            // 创建按钮
            startGameButton = new Button();
            startGameButton.setGraphic(startImageView);
            startGameButton.setPrefSize(START_BUTTON_WIDTH, START_BUTTON_HEIGHT);
            startGameButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand;"
            );
            
            // 添加悬停效果
            addButtonHoverEffect(startGameButton, startImageView);
            
            // 添加点击事件
            startGameButton.setOnAction(event -> startGame());
            
        } catch (Exception e) {
            System.err.println("无法加载开始游戏按钮图片: " + e.getMessage());
            // 创建文本按钮作为备用
            startGameButton = createTextButton("开始游戏", Color.web("#00D4AA"));
            startGameButton.setOnAction(event -> startGame());
        }
    }
    
    /**
     * 创建退出游戏按钮
     */
    private void createExitGameButton() {
        try {
            // 加载退出游戏按钮图片
            Image exitImage = new Image(getClass().getResourceAsStream("/mainview/exitgame.png"));
            ImageView exitImageView = new ImageView(exitImage);
            
            // 设置按钮尺寸 - 退出游戏按钮
            exitImageView.setFitWidth(EXIT_BUTTON_WIDTH);
            exitImageView.setFitHeight(EXIT_BUTTON_HEIGHT);
            exitImageView.setPreserveRatio(true); // 保持宽高比，按比例缩放
            
            // 创建按钮
            exitGameButton = new Button();
            exitGameButton.setGraphic(exitImageView);
            exitGameButton.setPrefSize(EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            exitGameButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand;"
            );
            
            // 添加悬停效果
            addButtonHoverEffect(exitGameButton, exitImageView);
            
            // 添加点击事件
            exitGameButton.setOnAction(event -> exitGame());
            
        } catch (Exception e) {
            System.err.println("无法加载退出游戏按钮图片: " + e.getMessage());
            // 创建文本按钮作为备用
            exitGameButton = createTextButton("退出游戏", Color.web("#FF6B6B"));
            exitGameButton.setOnAction(event -> exitGame());
        }
    }
    
    /**
     * 创建农场按钮
     */
    private void createFarmButton() {
        try {
            // 加载农场按钮图片
            Image farmImage = new Image(getClass().getResourceAsStream("/mainview/farm.png"));
            ImageView farmImageView = new ImageView(farmImage);
            
            // 设置按钮尺寸 - 农场按钮为小图标
            farmImageView.setFitWidth(FARM_BUTTON_WIDTH);
            farmImageView.setFitHeight(FARM_BUTTON_HEIGHT);
            farmImageView.setPreserveRatio(true); // 保持宽高比，按比例缩放
            
            // 创建按钮
            farmButton = new Button();
            farmButton.setGraphic(farmImageView);
            farmButton.setPrefSize(FARM_BUTTON_WIDTH, FARM_BUTTON_HEIGHT);
            farmButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand;"
            );
            
            // 添加悬停效果
            addButtonHoverEffect(farmButton, farmImageView);
            
            // 添加点击事件
            farmButton.setOnAction(event -> openFarm());
            
        } catch (Exception e) {
            System.err.println("无法加载农场按钮图片: " + e.getMessage());
            // 创建文本按钮作为备用
            farmButton = createTextButton("农场", Color.web("#4CAF50"));
            farmButton.setPrefSize(FARM_BUTTON_WIDTH, FARM_BUTTON_HEIGHT);
            farmButton.setOnAction(event -> openFarm());
        }
    }
    
    /**
     * 创建文本按钮（备用方案）
     */
    private Button createTextButton(String text, Color color) {
        Button button = new Button(text);
        button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: " + toHexColor(color) + "; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + toHexColor(color.darker()) + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-cursor: hand;"
        );
        
        // 添加悬停效果
        button.setOnMouseEntered(event -> {
            button.setStyle(
                "-fx-background-color: " + toHexColor(color.brighter()) + "; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + toHexColor(color) + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-cursor: hand;"
            );
        });
        
        button.setOnMouseExited(event -> {
            button.setStyle(
                "-fx-background-color: " + toHexColor(color) + "; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: " + toHexColor(color.darker()) + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10; " +
                "-fx-cursor: hand;"
            );
        });
        
        return button;
    }
    
    /**
     * 添加按钮悬停效果
     */
    private void addButtonHoverEffect(Button button, ImageView imageView) {
        button.setOnMouseEntered(event -> {
            // 放大效果
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
            
            // 透明度效果
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), imageView);
            fadeTransition.setToValue(0.8);
            fadeTransition.play();
        });
        
        button.setOnMouseExited(event -> {
            // 恢复原始大小
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            // 恢复原始透明度
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), imageView);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
        });
    }
    
    /**
     * 添加初始动画
     */
    private void addInitialAnimation() {
        // 设置初始状态
        buttonContainer.setOpacity(0);
        buttonContainer.setScaleX(0.8);
        buttonContainer.setScaleY(0.8);
        
        // 创建渐入动画
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), buttonContainer);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(500)); // 延迟500ms开始
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(800), buttonContainer);
        scaleTransition.setFromX(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToY(1.0);
        scaleTransition.setDelay(Duration.millis(500));
        
        // 并行播放动画
        javafx.animation.ParallelTransition parallelTransition = 
            new javafx.animation.ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
    }
    
    /**
     * 开始游戏
     */
    private void startGame() {
        System.out.println("开始游戏...");
        
        // 创建淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), buttonContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(event -> {
            // 显示地图选择界面
            try {
                MapSelectionView mapSelection = new MapSelectionView(primaryStage, this::onMapSelected);
                mapSelection.show();
            } catch (Exception e) {
                System.err.println("启动地图选择界面时出错: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        fadeOut.play();
    }
    
    /**
     * 地图选择回调
     */
    private void onMapSelected(String selectedMap) {
        System.out.println("选择地图: " + selectedMap);
        this.selectedMap = selectedMap; // 保存选择的地图
        
        // 显示人物选择界面
        try {
            CharacterSelectionView characterSelection = new CharacterSelectionView(primaryStage, selectedMap, this::onCharactersSelected);
            characterSelection.show();
        } catch (Exception e) {
            System.err.println("启动人物选择界面时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 人物选择回调
     */
    private void onCharactersSelected(java.util.Map<String, String> selectedCharacters) {
        System.out.println("选择人物: " + selectedCharacters);
        System.out.println("传递用户信息到游戏: " + (currentPlayer != null ? currentPlayer.getNickname() : "null"));
        
        // 启动游戏，传递选择的地图、人物和用户信息
        try {
            PlayerBombVisualTest game = new PlayerBombVisualTest();
            game.start(primaryStage, selectedMap, selectedCharacters, currentPlayer);
        } catch (Exception e) {
            System.err.println("启动游戏时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 退出游戏
     */
    private void exitGame() {
        System.out.println("退出游戏...");
        
        // 创建淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), buttonContainer);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(event -> {
            Platform.exit();
            System.exit(0);
        });
        
        fadeOut.play();
    }
    
    /**
     * 打开农场界面
     */
    private void openFarm() {
        System.out.println("打开农场界面...");
        
        try {
            // 创建农场界面
            com.stardewbombers.farm.FarmView farmView = new com.stardewbombers.farm.FarmView("player1", primaryStage);
            farmView.show();
        } catch (Exception e) {
            System.err.println("打开农场界面时出错: " + e.getMessage());
            e.printStackTrace();
            
            // 显示错误信息
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("无法打开农场界面");
            alert.setContentText("农场界面加载失败: " + e.getMessage());
            alert.showAndWait();
        }
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
     * 创建用户名显示 - 右上角黑色像素风格
     */
    private void createUsernameDisplay(Group root) {
        // 背景框尺寸和位置
        int bgWidth = 180;
        int bgHeight = 40;
        int bgX = WINDOW_WIDTH - bgWidth - 20; // 距离右边20像素
        int bgY = 20; // 距离顶部20像素
        
        // 添加背景框 - 像素风格
        javafx.scene.shape.Rectangle usernameBg = new javafx.scene.shape.Rectangle(bgWidth, bgHeight);
        usernameBg.setX(bgX);
        usernameBg.setY(bgY);
        usernameBg.setFill(Color.color(1, 1, 1, 0.9)); // 半透明白色背景
        usernameBg.setStroke(Color.BLACK);
        usernameBg.setStrokeWidth(3);
        usernameBg.setArcWidth(0); // 直角边框，像素风格
        usernameBg.setArcHeight(0);
        
        // 创建用户名标签 - 居中在背景框内
        usernameLabel = new javafx.scene.control.Label("Hi，玩家");
        usernameLabel.setLayoutX(bgX + 10); // 背景框左边距10像素
        usernameLabel.setLayoutY(bgY + 8);  // 背景框顶部边距8像素，考虑字体高度
        
        // 设置像素风格字体
        Font pixelFont = Font.font("Courier New", FontWeight.BOLD, 18);
        usernameLabel.setFont(pixelFont);
        
        // 设置黑色文字
        usernameLabel.setTextFill(Color.BLACK);
        
        // 添加像素风格的描边效果
        javafx.scene.effect.DropShadow pixelShadow = new javafx.scene.effect.DropShadow();
        pixelShadow.setRadius(0);
        pixelShadow.setColor(Color.WHITE);
        pixelShadow.setOffsetX(2);
        pixelShadow.setOffsetY(2);
        usernameLabel.setEffect(pixelShadow);
        
        // 将背景和标签添加到根节点
        root.getChildren().add(usernameBg);
        root.getChildren().add(usernameLabel);
        
        // 确保用户名显示在最前面
        usernameBg.toFront();
        usernameLabel.toFront();
        
        // 更新用户名显示
        if (currentPlayer != null && currentPlayer.getNickname() != null) {
            usernameLabel.setText("Hi，" + currentPlayer.getNickname());
            System.out.println("主菜单用户名显示已更新为: Hi，" + currentPlayer.getNickname());
        }
        
        System.out.println("主菜单用户名显示组件已创建");
    }
    
    /**
     * 显示主菜单
     */
    public void show() {
        primaryStage.setTitle("StardewBombers - 主菜单");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * 获取场景
     */
    public Scene getScene() {
        return menuScene;
    }
}
