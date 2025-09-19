package com.stardewbombers.farm;

import com.stardewbombers.character.CharacterUnlockManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Set;

public class FarmView {
    private final String playerId;
    private final Stage parentStage;
    private final FarmService farmService;
    private final Farm farm;
    private final GridPane farmGrid;
    private Label seed1Label, seed2Label, seed3Label;
    private Label crop1Label, crop2Label, crop3Label;
    private Label goldLabel;
    private final Timeline updateTimeline;
    private final StackPane[][] farmCells;
    
    // 使用单机版农场服务 - 不需要playerId
    private static SinglePlayerFarmService globalFarmService = null;
    
    /**
     * 获取单例农场服务实例
     */
    public static SinglePlayerFarmService getGlobalFarmService() {
        if (globalFarmService == null) {
            globalFarmService = SinglePlayerFarmService.getInstance();
        }
        return globalFarmService;
    }
    
    // 角色解锁管理器
    private final CharacterUnlockManager unlockManager = CharacterUnlockManager.getInstance();

    public FarmView(String playerId, Stage parentStage) {
        System.out.println("FarmView构造函数开始 - playerId: " + playerId);
        
        this.playerId = playerId; // 保留playerId参数以兼容现有调用
        this.parentStage = parentStage;
        
        // 使用单机版农场服务
        this.farmService = getGlobalFarmService();
        
        System.out.println("获取农场对象...");
        this.farm = farmService.getFarm(playerId);
        System.out.println("农场对象获取成功 - 尺寸: " + farm.getWidth() + "x" + farm.getHeight());
        
        this.farmGrid = new GridPane();
        this.updateTimeline = new Timeline();
        this.farmCells = new StackPane[farm.getHeight()][farm.getWidth()];
        
        System.out.println("FarmView构造函数完成");
    }

    public void show() {
        System.out.println("FarmView.show()方法开始");
        
        // 创建主容器
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB 0%, #98FB98 100%);");

        // 创建内容容器
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);"
        );

        // 标题
        Label titleLabel = new Label("🌱 我的农场 🌱");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2E8B57;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 2, 0, 0, 1);"
        );
        mainContainer.getChildren().add(titleLabel);

        // 资源显示 - 美化版本
        HBox resourceBox = new HBox(20);
        resourceBox.setAlignment(Pos.CENTER);
        
        // 种子卡片
        VBox seedCard = new VBox(10);
        seedCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #90EE90;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 15;"
        );
        seedCard.setAlignment(Pos.CENTER);
        
        Label seedTitle = new Label("🌱 种子库存");
        seedTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        
        seed1Label = new Label("🥕 胡萝卜种子: " + farmService.getSeedCount(Farm.CropType.CARROT));
        seed2Label = new Label("🍓 草莓种子: " + farmService.getSeedCount(Farm.CropType.STRAWBERRY));
        seed3Label = new Label("🥔 土豆种子: " + farmService.getSeedCount(Farm.CropType.POTATO));
        
        seed1Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        seed2Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        seed3Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        
        seedCard.getChildren().addAll(seedTitle, seed1Label, seed2Label, seed3Label);
        
        // 作物卡片
        VBox cropCard = new VBox(10);
        cropCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #90EE90;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 15;"
        );
        cropCard.setAlignment(Pos.CENTER);
        
        Label cropTitle = new Label("🌾 作物收获");
        cropTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        
        crop1Label = new Label("🥕 胡萝卜: " + farmService.getCropCount(Farm.CropType.CARROT));
        crop2Label = new Label("🍓 草莓: " + farmService.getCropCount(Farm.CropType.STRAWBERRY));
        crop3Label = new Label("🥔 土豆: " + farmService.getCropCount(Farm.CropType.POTATO));
        
        crop1Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        crop2Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        crop3Label.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        
        cropCard.getChildren().addAll(cropTitle, crop1Label, crop2Label, crop3Label);
        
        // 金币卡片
        VBox goldCard = new VBox(10);
        goldCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 15;"
        );
        goldCard.setAlignment(Pos.CENTER);
        
        Label goldTitle = new Label("💰 金币");
        goldTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #B8860B; -fx-font-weight: bold;");
        
        goldLabel = new Label("💰 金币: " + farmService.getGoldCount());
        goldLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #B8860B; -fx-font-weight: bold;");
        
        goldCard.getChildren().addAll(goldTitle, goldLabel);
        
        // 角色解锁状态卡片
        VBox unlockCard = new VBox(10);
        unlockCard.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #9B59B6;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 15;"
        );
        unlockCard.setAlignment(Pos.CENTER);
        
        Label unlockTitle = new Label("👥 角色解锁");
        unlockTitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #8E44AD; -fx-font-weight: bold;");
        
        // 显示解锁状态
        Set<String> unlockedCharacters = unlockManager.getUnlockedCharacters(playerId);
        Label unlockStatus = new Label("已解锁: " + unlockedCharacters.size() + "/8");
        unlockStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #8E44AD; -fx-font-weight: bold;");
        
        unlockCard.getChildren().addAll(unlockTitle, unlockStatus);
        
        resourceBox.getChildren().addAll(seedCard, cropCard, goldCard, unlockCard);
        mainContainer.getChildren().add(resourceBox);

        // 农场网格
        setupFarmGrid();
        mainContainer.getChildren().add(farmGrid);
        
        // 刷新农场网格显示
        refreshFarmGrid();

        // 操作说明
        Label instructionLabel = new Label("💡 点击空地种植，点击种子浇水，点击成熟作物收获");
        instructionLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #666666;" +
            "-fx-font-style: italic;" +
            "-fx-padding: 10;" +
            "-fx-background-color: rgba(255, 255, 255, 0.7);" +
            "-fx-background-radius: 5;"
        );
        mainContainer.getChildren().add(instructionLabel);

        // 按钮容器
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        // 刷新库存按钮
        Button refreshBtn = new Button("🔄 刷新库存");
        refreshBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #32CD32 0%, #228B22 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> {
            updateResourceLabels();
            showMessage("库存已刷新！");
        });
        
        // 重置库存按钮
        Button resetBtn = new Button("🗑️ 重置库存");
        resetBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FFA500 0%, #FF8C00 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        resetBtn.setOnAction(e -> {
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("重置库存");
            confirmDialog.setHeaderText("确定要重置库存吗？");
            confirmDialog.setContentText("这将重置所有种子和作物数量到初始值。");
            confirmDialog.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    ((SinglePlayerFarmService) farmService).resetInventory();
                    updateResourceLabels();
                    showMessage("库存已重置！");
                }
            });
        });
        
        // 返回按钮
        Button backBtn = new Button("🏠 返回主界面");
        backBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FF6347 0%, #DC143C 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> {
            updateTimeline.stop();
            Stage currentStage = (Stage) backBtn.getScene().getWindow();
            currentStage.close();
            // 返回主界面
            parentStage.show();
        });
        
        buttonBox.getChildren().addAll(refreshBtn, resetBtn, backBtn);
        mainContainer.getChildren().add(buttonBox);

        // 将主容器添加到根容器
        root.getChildren().add(mainContainer);

        Stage stage = new Stage();
        stage.setTitle("StardewBombers - 我的农场");
        Scene scene = new Scene(root, 700, 800);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // 设置定时更新
        setupUpdateTimeline();
        updateTimeline.play();
    }

    private void setupFarmGrid() {
        System.out.println("设置农场网格开始 - 农场尺寸: " + farm.getWidth() + "x" + farm.getHeight());
        
        farmGrid.setPadding(new Insets(15));
        farmGrid.setHgap(3);
        farmGrid.setVgap(3);
        farmGrid.setAlignment(Pos.CENTER);
        farmGrid.setStyle(
            "-fx-background-color: rgba(139, 69, 19, 0.1);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-padding: 15;"
        );

        for (int y = 0; y < farm.getHeight(); y++) {
            for (int x = 0; x < farm.getWidth(); x++) {
                StackPane cell = createFarmCell(x, y);
                farmCells[y][x] = cell;
                farmGrid.add(cell, x, y);
            }
        }
    }

    private StackPane createFarmCell(int x, int y) {
        StackPane cell = new StackPane();
        cell.setPrefSize(45, 45);
        cell.setStyle(
            "-fx-background-color: #F5DEB3;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #D2B48C;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        // 创建泥土背景
        ImageView floorBackground = new ImageView();
        floorBackground.setFitWidth(45);
        floorBackground.setFitHeight(45);
        floorBackground.setPreserveRatio(true);
        floorBackground.setSmooth(true);
        
        // 尝试加载地板图像，如果失败则使用默认样式
        Image floorImage = ImageManager.getFloorImage();
        if (floorImage != null) {
            floorBackground.setImage(floorImage);
        } else {
            // 如果图像加载失败，使用纯色背景
            floorBackground.setStyle("-fx-background-color: #8B4513;");
        }
        
        // 创建作物前景
        ImageView cropForeground = new ImageView();
        cropForeground.setFitWidth(45);
        cropForeground.setFitHeight(45);
        cropForeground.setPreserveRatio(true);
        cropForeground.setSmooth(true);
        
        // 将背景和前景添加到StackPane
        cell.getChildren().addAll(floorBackground, cropForeground);
        
        // 调试信息
        System.out.println("创建农场格子 - 位置(" + x + "," + y + "), 子节点数量: " + cell.getChildren().size());
        
        // 设置点击事件
        cell.setOnMouseClicked(e -> handleCellClick(x, y, e));
        
        // 设置悬停提示
        setupCellTooltip(cell, x, y);
        
        return cell;
    }

    private void updateCellAppearance(StackPane cell, int x, int y) {
        Farm.CropType cropType = farm.getCropType(x, y);
        Farm.CropState cropState = farm.getCropState(x, y);
        
        // 安全地获取前景ImageView（第二个子节点）
        ImageView cropForeground = null;
        System.out.println("更新格子外观 - 位置(" + x + "," + y + "), 子节点数量: " + cell.getChildren().size());
        
        if (cell.getChildren().size() > 1) {
            cropForeground = (ImageView) cell.getChildren().get(1);
            System.out.println("成功获取前景节点");
        } else {
            // 如果没有前景节点，创建一个
            System.out.println("子节点数量不足，创建前景节点");
            cropForeground = new ImageView();
            cropForeground.setFitWidth(45);
            cropForeground.setFitHeight(45);
            cropForeground.setPreserveRatio(true);
            cropForeground.setSmooth(true);
            cell.getChildren().add(cropForeground);
            System.out.println("前景节点创建完成，新的子节点数量: " + cell.getChildren().size());
        }
        
        // 根据状态设置作物图像
        if (cropState == Farm.CropState.EMPTY || cropState == Farm.CropState.DEAD) {
            // 空地或枯死状态，不显示作物图像
            cropForeground.setImage(null);
        } else {
            // 显示对应的种子或果实图像
            Image cropImage = ImageManager.getCropImage(cropType, cropState);
            System.out.println("设置作物图像 - 位置: (" + x + "," + y + "), 类型: " + cropType + ", 状态: " + cropState + ", 图像: " + (cropImage != null ? "成功" : "失败"));
            cropForeground.setImage(cropImage);
        }
        
        // 根据状态设置透明度和样式类
        switch (cropState) {
            case EMPTY -> {
                cropForeground.setOpacity(0.0);
                cell.setStyle(
                    "-fx-background-color: #F5DEB3;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-color: #D2B48C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-cursor: hand;"
                );
            }
            case PLANTED -> {
                cropForeground.setOpacity(0.8);
                cell.setStyle(
                    "-fx-background-color: #8B4513;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-color: #D2B48C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-cursor: hand;"
                );
            }
            case WATERED -> {
                cropForeground.setOpacity(0.9);
                cell.setStyle(
                    "-fx-background-color: #4169E1;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-color: #D2B48C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-cursor: hand;"
                );
            }
            case READY -> {
                cropForeground.setOpacity(1.0);
                cell.setStyle(
                    "-fx-background-color: #32CD32;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-color: #D2B48C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-cursor: hand;"
                );
            }
            case DEAD -> {
                cropForeground.setOpacity(0.0);
                cell.setStyle(
                    "-fx-background-color: #DC143C;" +
                    "-fx-background-radius: 5;" +
                    "-fx-border-color: #D2B48C;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 5;" +
                    "-fx-cursor: hand;"
                );
            }
        }
    }
    
    private void setupCellTooltip(StackPane cell, int x, int y) {
        Farm.CropType cropType = farm.getCropType(x, y);
        Farm.CropState cropState = farm.getCropState(x, y);
        
        String tooltipText = getCellTooltipText(cropType, cropState, x, y);
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.8);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 8;"
        );
        Tooltip.install(cell, tooltip);
    }
    
    private String getCellTooltipText(Farm.CropType cropType, Farm.CropState cropState, int x, int y) {
        switch (cropState) {
            case EMPTY:
                return "空地 - 点击种植";
            case PLANTED:
                long remainingDeathTime = farm.getRemainingDeathTime(x, y);
                return String.format("%s种子 - 需要浇水 (剩余时间: %d小时)", 
                    getCropTypeName(cropType), remainingDeathTime);
            case WATERED:
                long remainingGrowTime = farm.getRemainingGrowTime(x, y);
                return String.format("%s - 生长中 (剩余时间: %d分钟)", 
                    getCropTypeName(cropType), remainingGrowTime);
            case READY:
                return String.format("%s - 可收获", getCropTypeName(cropType));
            case DEAD:
                return "枯死的作物 - 点击清理";
            default:
                return "";
        }
    }
    
    private String getCropTypeName(Farm.CropType cropType) {
        switch (cropType) {
            case CARROT: return "胡萝卜";
            case STRAWBERRY: return "草莓";
            case POTATO: return "土豆";
            default: return "未知";
        }
    }

    private void handleCellClick(int x, int y, MouseEvent event) {
        Farm.CropState cropState = farm.getCropState(x, y);
        
        // 安全检查：确保数组索引在范围内
        if (y >= farmCells.length || x >= farmCells[y].length) {
            System.err.println("数组越界: y=" + y + ", x=" + x + ", farmCells.length=" + farmCells.length);
            return;
        }
        
        StackPane cell = farmCells[y][x];
        
        // 添加点击动画效果
        addClickAnimation(cell);
        
        if (cropState == Farm.CropState.EMPTY) {
            // 显示种植选择对话框
            showPlantDialog(x, y);
        } else if (cropState == Farm.CropState.PLANTED) {
            // 浇水
            waterCrop(x, y);
        } else if (cropState == Farm.CropState.READY) {
            // 收获作物
            harvestCrop(x, y);
        } else if (cropState == Farm.CropState.DEAD) {
            // 清理枯死的作物
            clearDeadCrop(x, y);
        }
    }
    
    /**
     * 添加点击动画效果
     */
    private void addClickAnimation(StackPane cell) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), cell);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.9);
        scaleTransition.setToY(0.9);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }
    
    /**
     * 添加种植动画效果
     */
    private void addPlantAnimation(StackPane cell) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), cell);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), cell);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
    }
    
    /**
     * 添加收获动画效果
     */
    private void addHarvestAnimation(StackPane cell) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), cell);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);
        scaleTransition.play();
    }

    private void showPlantDialog(int x, int y) {
        Alert dialog = new Alert(AlertType.CONFIRMATION);
        dialog.setTitle("🌱 种植作物");
        dialog.setHeaderText("选择要种植的种子");
        
        ButtonType carrotBtn = new ButtonType("🥕 胡萝卜种子");
        ButtonType strawberryBtn = new ButtonType("🍓 草莓种子");
        ButtonType potatoBtn = new ButtonType("🥔 土豆种子");
        ButtonType cancelBtn = new ButtonType("❌ 取消");
        
        dialog.getButtonTypes().setAll(carrotBtn, strawberryBtn, potatoBtn, cancelBtn);
        
        dialog.showAndWait().ifPresent(buttonType -> {
            Farm.CropType cropType = null;
            if (buttonType == carrotBtn) {
                cropType = Farm.CropType.CARROT;
            } else if (buttonType == strawberryBtn) {
                cropType = Farm.CropType.STRAWBERRY;
            } else if (buttonType == potatoBtn) {
                cropType = Farm.CropType.POTATO;
            }
            
            if (cropType != null) {
                plantSeed(x, y, cropType);
            }
        });
    }

    private void plantSeed(int x, int y, Farm.CropType cropType) {
        // 检查是否有对应的种子
        if (farmService.getSeedCount(cropType) <= 0) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("种植失败");
            alert.setHeaderText("种子不足！");
            alert.showAndWait();
            return;
        }
        
        // 种植
        if (farmService.plantSeed(playerId, x, y, cropType)) {
            // 更新显示
            updateResourceLabels();
            refreshFarmGrid();
            
            // 添加种植动画
            if (y < farmCells.length && x < farmCells[y].length) {
                StackPane cell = farmCells[y][x];
                addPlantAnimation(cell);
            }
        }
    }

    private void waterCrop(int x, int y) {
        if (farmService.waterCrop(playerId, x, y)) {
            refreshFarmGrid();
            showMessage("浇水成功！作物将在5分钟后成熟。");
        } else {
            showMessage("浇水失败！");
        }
    }
    
    private void harvestCrop(int x, int y) {
        Farm.CropType cropType = farmService.harvestCrop(playerId, x, y);
        if (cropType != Farm.CropType.NONE) {
            // 添加收获动画
            if (y < farmCells.length && x < farmCells[y].length) {
                StackPane cell = farmCells[y][x];
                addHarvestAnimation(cell);
            }
            
            // 获取金币奖励
            int goldReward = getGoldReward(cropType);
            
            // 检查角色解锁
            int currentGold = farmService.getGoldCount();
            Set<String> newlyUnlocked = unlockManager.checkAndUnlockCharacters("singlePlayer", currentGold);
            
            updateResourceLabels();
            refreshFarmGrid();
            
            // 显示收获消息
            String message = "收获成功！获得" + getCropTypeName(cropType) + "！获得金币: " + goldReward;
            
            // 如果有新解锁的角色，显示解锁通知
            if (!newlyUnlocked.isEmpty()) {
                StringBuilder unlockMessage = new StringBuilder("\n\n🎉 新角色解锁！\n");
                for (String character : newlyUnlocked) {
                    unlockMessage.append("• ").append(unlockManager.getCharacterDisplayName(character)).append("\n");
                }
                message += unlockMessage.toString();
            }
            
            showMessage(message);
        }
    }
    
    /**
     * 根据作物类型获取金币奖励
     */
    private int getGoldReward(Farm.CropType cropType) {
        switch (cropType) {
            case CARROT:
                return 1; // 胡萝卜1个金币
            case STRAWBERRY:
                return 2; // 草莓2个金币
            case POTATO:
                return 3; // 土豆3个金币
            default:
                return 0;
        }
    }
    
    private void clearDeadCrop(int x, int y) {
        if (farmService.clearDeadCrop(playerId, x, y)) {
            refreshFarmGrid();
            showMessage("清理完成！");
        }
    }
    
    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("💡 提示");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void updateResourceLabels() {
        seed1Label.setText("🥕 胡萝卜种子: " + farmService.getSeedCount(Farm.CropType.CARROT));
        seed2Label.setText("🍓 草莓种子: " + farmService.getSeedCount(Farm.CropType.STRAWBERRY));
        seed3Label.setText("🥔 土豆种子: " + farmService.getSeedCount(Farm.CropType.POTATO));
        crop1Label.setText("🥕 胡萝卜: " + farmService.getCropCount(Farm.CropType.CARROT));
        crop2Label.setText("🍓 草莓: " + farmService.getCropCount(Farm.CropType.STRAWBERRY));
        crop3Label.setText("🥔 土豆: " + farmService.getCropCount(Farm.CropType.POTATO));
        goldLabel.setText("💰 金币: " + farmService.getGoldCount());
    }

    private void refreshFarmGrid() {
        for (int y = 0; y < farm.getHeight(); y++) {
            for (int x = 0; x < farm.getWidth(); x++) {
                // 安全检查：确保数组索引在范围内
                if (y < farmCells.length && x < farmCells[y].length) {
                    StackPane cell = farmCells[y][x];
                    if (cell != null) {
                        updateCellAppearance(cell, x, y);
                        setupCellTooltip(cell, x, y);
                    }
                }
            }
        }
    }

    private void setupUpdateTimeline() {
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            farmService.updateFarm(playerId);
            refreshFarmGrid();
        }));
    }
}