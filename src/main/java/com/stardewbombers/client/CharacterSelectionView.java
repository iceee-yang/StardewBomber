package com.stardewbombers.client;

import com.stardewbombers.farm.SimpleFarmService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

/**
 * 人物选择界面
 */
public class CharacterSelectionView {
    private final Stage parentStage;
    private final String selectedMap;
    private final Consumer<Map<String, String>> onCharactersSelected;
    
    // 界面尺寸
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;
    
    // 所有角色（包括可解锁的）
    private static final String[] ALL_CHARACTERS = {
        "Abigail", "Alex", "Haley", "Lewis", 
        "Demetrius", "Leah", "Penny", "Sebastian"
    };
    private static final String[] CHARACTER_NAMES = {
        "阿比盖尔", "亚历克斯", "海莉", "刘易斯",
        "德米特里", "莉亚", "佩妮", "塞巴斯蒂安"
    };
    private static final String[] CHARACTER_DESCRIPTIONS = {
        "勇敢的冒险家，喜欢探索",
        "运动健将，充满活力",
        "美丽的艺术家，富有创造力",
        "经验丰富的镇长，智慧过人",
        "科学家，善于研究",
        "艺术家，热爱自然",
        "教师，温柔善良",
        "程序员，神秘内向"
    };
    
    // 角色解锁要求
    private static final Map<String, Integer> CHARACTER_UNLOCK_REQUIREMENTS = new HashMap<>();
    static {
        CHARACTER_UNLOCK_REQUIREMENTS.put("Demetrius", 10);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Leah", 30);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Penny", 50);
        CHARACTER_UNLOCK_REQUIREMENTS.put("Sebastian", 100);
    }
    
    // 农场服务
    private final SimpleFarmService farmService;
    
    // 选择状态
    private final Map<String, String> selectedCharacters = new HashMap<>();
    private final Set<String> usedCharacters = new HashSet<>();
    private int currentPlayer = 1;
    private final int totalPlayers = 3;
    
    // UI组件
    private Label instructionLabel;
    private HBox characterContainer;
    private Button confirmButton;
    private Button backButton;
    
    public CharacterSelectionView(Stage parentStage, String selectedMap, Consumer<Map<String, String>> onCharactersSelected) {
        this.parentStage = parentStage;
        this.selectedMap = selectedMap;
        this.onCharactersSelected = onCharactersSelected;
        this.farmService = new SimpleFarmService();
    }
    
    public void show() {
        // 创建根容器
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50 0%, #34495E 100%);");
        
        // 标题
        Label titleLabel = new Label("👥 选择人物");
        titleLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #ECF0F1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 3, 0, 0, 2);"
        );
        root.getChildren().add(titleLabel);
        
        // 地图信息
        Label mapInfoLabel = new Label("已选择地图: " + getMapDisplayName(selectedMap));
        mapInfoLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: #BDC3C7;"
        );
        root.getChildren().add(mapInfoLabel);
        
        // 选择说明
        instructionLabel = new Label("Player " + currentPlayer + " 请选择人物");
        instructionLabel.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3498DB;"
        );
        root.getChildren().add(instructionLabel);
        
        // 人物选择容器 - 使用ScrollPane支持滚动
        characterContainer = new HBox(15);
        characterContainer.setAlignment(Pos.CENTER);
        characterContainer.setPadding(new Insets(10));
        
        // 创建ScrollPane来包装角色容器
        ScrollPane scrollPane = new ScrollPane(characterContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setPrefWidth(WINDOW_WIDTH - 100);
        scrollPane.setPrefHeight(200);
        
        // 确保HBox不会换行，保持水平布局
        characterContainer.setMaxWidth(Double.MAX_VALUE);
        
        // 设置ScrollPane的内容区域大小，确保水平滚动
        characterContainer.setMinWidth(8 * 160); // 8个角色 * 每个角色160px宽度
        
        updateCharacterDisplay();
        root.getChildren().add(scrollPane);
        
        // 已选择人物显示
        VBox selectedContainer = new VBox(10);
        selectedContainer.setAlignment(Pos.CENTER);
        updateSelectedDisplay(selectedContainer);
        root.getChildren().add(selectedContainer);
        
        // 按钮容器
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        
        // 返回按钮
        backButton = new Button("🔙 返回地图选择");
        backButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #E74C3C 0%, #C0392B 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            // 返回地图选择界面
            MapSelectionView mapSelection = new MapSelectionView(parentStage, this::onMapSelected);
            mapSelection.show();
        });
        
        // 确认按钮（初始隐藏）
        confirmButton = new Button("✅ 开始游戏");
        confirmButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #27AE60 0%, #229954 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24;" +
            "-fx-cursor: hand;"
        );
        confirmButton.setVisible(false);
        confirmButton.setOnAction(e -> {
            System.out.println("开始游戏 - 地图: " + selectedMap + ", 人物: " + selectedCharacters);
            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
            currentStage.close();
            onCharactersSelected.accept(new HashMap<>(selectedCharacters));
        });
        
        buttonContainer.getChildren().addAll(backButton, confirmButton);
        root.getChildren().add(buttonContainer);
        
        // 创建场景和舞台
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        Stage stage = new Stage();
        stage.setTitle("StardewBombers - 选择人物");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        // 添加进入动画
        addEnterAnimation(root);
    }
    
    private void updateCharacterDisplay() {
        characterContainer.getChildren().clear();
        
        // 获取当前玩家的解锁状态（使用默认玩家ID）
        String playerId = "defaultPlayer";
        Set<String> unlockedCharacters = farmService.getUnlockedCharacters(playerId);
        
        for (int i = 0; i < ALL_CHARACTERS.length; i++) {
            String character = ALL_CHARACTERS[i];
            boolean isUsed = usedCharacters.contains(character);
            boolean isUnlocked = unlockedCharacters.contains(character);
            
            VBox characterCard = createCharacterCard(i, isUsed, isUnlocked);
            characterContainer.getChildren().add(characterCard);
        }
    }
    
    private VBox createCharacterCard(int index, boolean isUsed, boolean isUnlocked) {
        String character = ALL_CHARACTERS[index];
        
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setMinWidth(140);
        card.setMaxWidth(140);
        
        // 根据是否已使用和解锁状态设置样式
        if (isUsed) {
            card.setStyle(
                "-fx-background-color: rgba(149, 165, 166, 0.7);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #95A5A6;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;"
            );
        } else if (!isUnlocked) {
            // 未解锁状态
            card.setStyle(
                "-fx-background-color: rgba(149, 165, 166, 0.5);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #7F8C8D;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;"
            );
        } else {
            // 已解锁状态
            card.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #3498DB;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);"
            );
        }
        
        // 人物头像
        ImageView characterImage = new ImageView();
        characterImage.setFitWidth(100);
        characterImage.setFitHeight(100);
        characterImage.setPreserveRatio(true);
        characterImage.setSmooth(true);
        
        // 尝试加载人物图像
        try {
            String imagePath = getCharacterImagePath(character);
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            if (image != null && !image.isError()) {
                characterImage.setImage(image);
            } else {
                // 使用默认图像
                characterImage.setStyle("-fx-background-color: #BDC3C7;");
            }
        } catch (Exception e) {
            // 使用默认样式
            characterImage.setStyle("-fx-background-color: #BDC3C7;");
        }
        
        // 人物名称
        Label characterName = new Label(CHARACTER_NAMES[index]);
        String nameColor = isUsed ? "#7F8C8D" : (!isUnlocked ? "#95A5A6" : "#2C3E50");
        characterName.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + nameColor + ";"
        );
        
        // 人物描述
        String description = CHARACTER_DESCRIPTIONS[index];
        if (!isUnlocked) {
            Integer requiredGold = CHARACTER_UNLOCK_REQUIREMENTS.get(character);
            if (requiredGold != null) {
                description = "需要 " + requiredGold + " 金币解锁";
            }
        }
        Label characterDescription = new Label(description);
        String descColor = isUsed ? "#95A5A6" : (!isUnlocked ? "#BDC3C7" : "#7F8C8D");
        characterDescription.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + descColor + ";" +
            "-fx-wrap-text: true;"
        );
        characterDescription.setMaxWidth(120);
        
        // 选择按钮
        String buttonText;
        boolean isButtonDisabled;
        String buttonStyle;
        
        if (isUsed) {
            buttonText = "已选择";
            isButtonDisabled = true;
            buttonStyle = "-fx-background-color: #95A5A6;";
        } else if (!isUnlocked) {
            buttonText = "未解锁";
            isButtonDisabled = true;
            buttonStyle = "-fx-background-color: #BDC3C7;";
        } else {
            buttonText = "选择";
            isButtonDisabled = false;
            buttonStyle = "-fx-background-color: linear-gradient(to bottom, #27AE60 0%, #229954 100%);";
        }
        
        Button selectButton = new Button(buttonText);
        selectButton.setDisable(isButtonDisabled);
        selectButton.setStyle(
            buttonStyle +
            "-fx-background-radius: 6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: " + (isButtonDisabled ? "default" : "hand") + ";"
        );
        
        if (!isButtonDisabled) {
            selectButton.setOnAction(e -> selectCharacter(character));
            addCardHoverEffect(card, selectButton);
        }
        
        card.getChildren().addAll(characterImage, characterName, characterDescription, selectButton);
        
        return card;
    }
    
    private void selectCharacter(String character) {
        // 记录选择
        selectedCharacters.put("player" + currentPlayer, character);
        usedCharacters.add(character);
        
        System.out.println("Player " + currentPlayer + " 选择了: " + character);
        
        // 移动到下一个玩家
        currentPlayer++;
        
        if (currentPlayer <= totalPlayers) {
            // 更新界面
            instructionLabel.setText("Player " + currentPlayer + " 请选择人物");
            updateCharacterDisplay();
        } else {
            // 所有玩家都选择完毕
            instructionLabel.setText("所有玩家选择完毕！");
            instructionLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #27AE60;"
            );
            confirmButton.setVisible(true);
            characterContainer.setVisible(false);
        }
    }
    
    private void updateSelectedDisplay(VBox container) {
        container.getChildren().clear();
        
        Label selectedTitle = new Label("已选择的人物:");
        selectedTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #ECF0F1;"
        );
        container.getChildren().add(selectedTitle);
        
        HBox selectedList = new HBox(20);
        selectedList.setAlignment(Pos.CENTER);
        
        for (Map.Entry<String, String> entry : selectedCharacters.entrySet()) {
            String player = entry.getKey();
            String character = entry.getValue();
            
            Label playerLabel = new Label(player + ": " + getCharacterDisplayName(character));
            playerLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #3498DB;" +
                "-fx-background-color: rgba(52, 152, 219, 0.2);" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 5 10;"
            );
            selectedList.getChildren().add(playerLabel);
        }
        
        container.getChildren().add(selectedList);
    }
    
    private void addCardHoverEffect(VBox card, Button button) {
        card.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
                scaleTransition.setToX(1.05);
                scaleTransition.setToY(1.05);
                scaleTransition.play();
            }
        });
        
        card.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
                scaleTransition.setToX(1.0);
                scaleTransition.setToY(1.0);
                scaleTransition.play();
            }
        });
    }
    
    private void addEnterAnimation(VBox root) {
        root.setOpacity(0);
        root.setScaleX(0.8);
        root.setScaleY(0.8);
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(800), root);
        fadeTransition.setToValue(1.0);
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(800), root);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        
        fadeTransition.play();
        scaleTransition.play();
    }
    
    private String getMapDisplayName(String mapName) {
        switch (mapName) {
            case "cave_map": return "洞穴地图";
            case "home_map": return "家园地图";
            case "farm_map": return "农场地图";
            default: return mapName;
        }
    }
    
    private String getCharacterDisplayName(String characterName) {
        for (int i = 0; i < ALL_CHARACTERS.length; i++) {
            if (ALL_CHARACTERS[i].equals(characterName)) {
                return CHARACTER_NAMES[i];
            }
        }
        return characterName;
    }
    
    private void onMapSelected(String mapName) {
        // 这个方法用于从地图选择界面返回时重新创建人物选择界面
        CharacterSelectionView characterSelection = new CharacterSelectionView(parentStage, mapName, onCharactersSelected);
        characterSelection.show();
    }
    
    /**
     * 获取人物图像路径
     */
    private String getCharacterImagePath(String characterName) {
        switch (characterName) {
            case "Abigail":
                return "/character(16-32)/Abigail_big.png";
            case "Alex":
                return "/character(16-32)/Alex_big.png";
            case "Haley":
                return "/character(16-32)/Haley_big.png";
            case "Lewis":
                return "/character(16-32)/Lewis_big.png";
            case "Demetrius":
                return "/character(16-32)/Demetrius_big.png";
            case "Leah":
                return "/character(16-32)/Leah_big.png";
            case "Penny":
                return "/character(16-32)/Penny_big.png";
            case "Sebastian":
                return "/character(16-32)/Sebastian_big.png";
            default:
                return "/character(16-32)/Abigail_big.png"; // 默认使用阿比盖尔图像
        }
    }
}
