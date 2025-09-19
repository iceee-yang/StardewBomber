package com.stardewbombers.client;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * 地图选择界面
 */
public class MapSelectionView {
    private final Stage parentStage;
    private final Consumer<String> onMapSelected;
    
    // 界面尺寸
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    
    // 地图信息
    private static final String[] MAPS = {"cave_map", "home_map", "farm_map"};
    private static final String[] MAP_NAMES = {"洞穴地图", "家园地图", "农场地图"};
    private static final String[] MAP_DESCRIPTIONS = {
        "神秘的洞穴，充满挑战",
        "温馨的家园，适合新手",
        "广阔的农场，自由探索"
    };
    
    public MapSelectionView(Stage parentStage, Consumer<String> onMapSelected) {
        this.parentStage = parentStage;
        this.onMapSelected = onMapSelected;
    }
    
    public void show() {
        // 创建根容器
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50 0%, #34495E 100%);");
        
        // 标题
        Label titleLabel = new Label("🗺️ 选择地图");
        titleLabel.setStyle(
            "-fx-font-size: 36px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #ECF0F1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 3, 0, 0, 2);"
        );
        root.getChildren().add(titleLabel);
        
        // 地图选择容器
        HBox mapContainer = new HBox(30);
        mapContainer.setAlignment(Pos.CENTER);
        
        // 创建地图选择卡片
        for (int i = 0; i < MAPS.length; i++) {
            VBox mapCard = createMapCard(i);
            mapContainer.getChildren().add(mapCard);
        }
        
        root.getChildren().add(mapContainer);
        
        // 返回按钮
        Button backButton = new Button("🔙 返回主菜单");
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
            parentStage.show();
        });
        
        root.getChildren().add(backButton);
        
        // 创建场景和舞台
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        Stage stage = new Stage();
        stage.setTitle("StardewBombers - 选择地图");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        // 添加进入动画
        addEnterAnimation(root);
    }
    
    private VBox createMapCard(int index) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 3);"
        );
        
        // 地图预览图像
        ImageView mapPreview = new ImageView();
        mapPreview.setFitWidth(200);
        mapPreview.setFitHeight(150);
        mapPreview.setPreserveRatio(true);
        mapPreview.setSmooth(true);
        
        // 尝试加载地图预览图像
        try {
            String imagePath = getMapImagePath(MAPS[index]);
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            if (image != null && !image.isError()) {
                mapPreview.setImage(image);
            } else {
                // 使用默认图像
                mapPreview.setStyle("-fx-background-color: #BDC3C7;");
            }
        } catch (Exception e) {
            // 使用默认样式
            mapPreview.setStyle("-fx-background-color: #BDC3C7;");
        }
        
        // 地图名称
        Label mapName = new Label(MAP_NAMES[index]);
        mapName.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2C3E50;"
        );
        
        // 地图描述
        Label mapDescription = new Label(MAP_DESCRIPTIONS[index]);
        mapDescription.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #7F8C8D;" +
            "-fx-wrap-text: true;"
        );
        mapDescription.setMaxWidth(180);
        
        // 选择按钮
        Button selectButton = new Button("选择");
        selectButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #27AE60 0%, #229954 100%);" +
            "-fx-background-radius: 6;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
        
        selectButton.setOnAction(e -> {
            String selectedMap = MAPS[index];
            System.out.println("选择地图: " + selectedMap);
            
            // 添加选择动画
            addSelectAnimation(card, () -> {
                Stage currentStage = (Stage) selectButton.getScene().getWindow();
                currentStage.close();
                onMapSelected.accept(selectedMap);
            });
        });
        
        // 添加悬停效果
        addCardHoverEffect(card, selectButton);
        
        card.getChildren().addAll(mapPreview, mapName, mapDescription, selectButton);
        
        return card;
    }
    
    private void addCardHoverEffect(VBox card, Button button) {
        card.setOnMouseEntered(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    private void addSelectAnimation(VBox card, Runnable onComplete) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), card);
        scaleTransition.setToX(0.9);
        scaleTransition.setToY(0.9);
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), card);
        fadeTransition.setToValue(0.0);
        
        scaleTransition.setOnFinished(e -> onComplete.run());
        
        scaleTransition.play();
        fadeTransition.play();
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
    
    /**
     * 获取地图图像路径
     */
    private String getMapImagePath(String mapName) {
        switch (mapName) {
            case "farm_map":
                return "/textures/farm.jpg";
            case "home_map":
                return "/textures/home.jpg";
            case "cave_map":
                return "/textures/cave.png";
            default:
                return "/textures/farm.jpg"; // 默认使用农场图像
        }
    }
}
