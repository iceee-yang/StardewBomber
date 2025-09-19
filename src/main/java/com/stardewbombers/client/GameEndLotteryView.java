package com.stardewbombers.client;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import com.stardewbombers.farm.Farm.CropType;
import com.stardewbombers.farm.SinglePlayerFarmService;
import com.stardewbombers.model.Player;
import java.util.Random;

/**
 * 游戏结束抽奖界面
 */
public class GameEndLotteryView {
    private final Stage parentStage;
    private final String playerId;
    private final SinglePlayerFarmService farmService;
    private final Player currentPlayer; // 添加当前用户信息
    private final Random random = new Random();
    
    // 种子概率配置
    private static final double CARROT_PROBABILITY = 0.5;    // 50% 胡萝卜种子
    private static final double STRAWBERRY_PROBABILITY = 0.3; // 30% 草莓种子
    private static final double POTATO_PROBABILITY = 0.2;     // 20% 土豆种子
    
    // 数量概率配置
    private static final double ONE_SEED_PROBABILITY = 0.7;   // 70% 获得1个
    private static final double TWO_SEED_PROBABILITY = 0.25;  // 25% 获得2个
    private static final double THREE_SEED_PROBABILITY = 0.05; // 5% 获得3个
    
    public GameEndLotteryView(Stage parentStage, String playerId, SinglePlayerFarmService farmService) {
        this.parentStage = parentStage;
        this.playerId = playerId; // 保留参数以兼容现有调用
        this.farmService = farmService;
        this.currentPlayer = null; // 默认无用户信息
    }
    
    public GameEndLotteryView(Stage parentStage, String playerId, SinglePlayerFarmService farmService, Player currentPlayer) {
        this.parentStage = parentStage;
        this.playerId = playerId;
        this.farmService = farmService;
        this.currentPlayer = currentPlayer;
    }
    
    public void show() {
        Stage lotteryStage = new Stage();
        lotteryStage.initOwner(parentStage);
        lotteryStage.initModality(Modality.APPLICATION_MODAL);
        lotteryStage.initStyle(StageStyle.UNDECORATED);
        lotteryStage.setTitle("游戏结算 - 抽奖");
        
        // 创建主容器
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FFD700 0%, #FFA500 50%, #FF8C00 100%);" +
            "-fx-background-radius: 20;"
        );
        
        // 标题
        Label titleLabel = new Label("🎉 游戏结束！抽奖时间 🎉");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #8B4513;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 3, 0, 0, 1);"
        );
        
        // 抽奖按钮
        Button lotteryBtn = new Button("🎲 开始抽奖");
        lotteryBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #32CD32 0%, #228B22 100%);" +
            "-fx-background-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 15 30;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
        );
        
        // 结果显示区域
        VBox resultBox = new VBox(15);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 15;"
        );
        
        Label resultLabel = new Label("点击按钮开始抽奖！");
        resultLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2E8B57;"
        );
        
        resultBox.getChildren().add(resultLabel);
        
        // 按钮容器
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button closeBtn = new Button("返回主界面");
        closeBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #FF6347 0%, #DC143C 100%);" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> {
            lotteryStage.close();
            // 返回到主界面而不是关闭游戏，传递用户信息
            if (currentPlayer != null) {
                MainMenuView mainMenu = new MainMenuView(parentStage, currentPlayer);
                mainMenu.show();
            } else {
                MainMenuView mainMenu = new MainMenuView(parentStage);
                mainMenu.show();
            }
        });
        
        buttonBox.getChildren().addAll(lotteryBtn, closeBtn);
        
        // 设置抽奖按钮事件
        lotteryBtn.setOnAction(e -> {
            System.out.println("抽奖按钮被点击");
            lotteryBtn.setDisable(true);
            lotteryBtn.setText("抽奖中...");
            
            // 直接执行抽奖，不使用动画
            performLotteryDirect(resultLabel, lotteryBtn);
        });
        
        // 组装界面
        root.getChildren().addAll(titleLabel, lotteryBtn, resultBox, buttonBox);
        
        Scene scene = new Scene(root, 450, 400);
        lotteryStage.setScene(scene);
        lotteryStage.setResizable(false);
        lotteryStage.show();
    }
    
    /**
     * 执行抽奖
     */
    private void performLottery(Label resultLabel, Button lotteryBtn) {
        System.out.println("开始执行抽奖");
        
        // 显示抽奖中状态
        resultLabel.setText("抽奖中...");
        System.out.println("设置抽奖中文本");
        
        // 使用简单的延迟来模拟抽奖过程
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        timeline.getKeyFrames().add(new javafx.animation.KeyFrame(Duration.millis(500), e -> {
            System.out.println("延迟结束，开始计算结果");
            try {
                // 执行抽奖逻辑
                LotteryResult result = calculateLotteryResult();
                System.out.println("抽奖结果: " + result.cropType + " x" + result.quantity);
                
                // 更新农场库存 - 直接操作全局数据
                farmService.addSeed(result.cropType, result.quantity);
                System.out.println("已更新农场库存");
                
                // 显示结果
                String resultText = String.format("🎉 恭喜！获得 %d 个 %s种子！", 
                    result.quantity, getCropTypeName(result.cropType));
                resultLabel.setText(resultText);
                System.out.println("显示结果: " + resultText);
                
                // 恢复按钮状态
                lotteryBtn.setDisable(false);
                lotteryBtn.setText("🎲 重新抽奖");
                System.out.println("按钮状态已恢复");
            } catch (Exception ex) {
                System.err.println("抽奖过程中出错: " + ex.getMessage());
                ex.printStackTrace();
                resultLabel.setText("抽奖失败，请重试");
                lotteryBtn.setDisable(false);
                lotteryBtn.setText("🎲 重新抽奖");
            }
        }));
        
        System.out.println("开始播放时间线");
        timeline.play();
    }
    
    /**
     * 直接执行抽奖（不使用动画）
     */
    private void performLotteryDirect(Label resultLabel, Button lotteryBtn) {
        System.out.println("开始直接执行抽奖");
        
        try {
            // 执行抽奖逻辑
            LotteryResult result = calculateLotteryResult();
            System.out.println("抽奖结果: " + result.cropType + " x" + result.quantity);
            
            // 更新农场库存 - 直接操作全局数据
            farmService.addSeed(result.cropType, result.quantity);
            System.out.println("已更新农场库存");
            
            // 显示结果
            String resultText = String.format("🎉 恭喜！获得 %d 个 %s种子！", 
                result.quantity, getCropTypeName(result.cropType));
            resultLabel.setText(resultText);
            System.out.println("显示结果: " + resultText);
            
            // 恢复按钮状态
            lotteryBtn.setDisable(false);
            lotteryBtn.setText("🎲 重新抽奖");
            System.out.println("按钮状态已恢复");
        } catch (Exception ex) {
            System.err.println("抽奖过程中出错: " + ex.getMessage());
            ex.printStackTrace();
            resultLabel.setText("抽奖失败，请重试");
            lotteryBtn.setDisable(false);
            lotteryBtn.setText("🎲 重新抽奖");
        }
    }
    
    /**
     * 计算抽奖结果
     */
    private LotteryResult calculateLotteryResult() {
        // 根据概率确定种子类型
        double seedRandom = random.nextDouble();
        CropType cropType;
        
        System.out.println("种子随机数: " + seedRandom);
        
        if (seedRandom < CARROT_PROBABILITY) {
            cropType = CropType.CARROT; // 胡萝卜 50%
            System.out.println("选择胡萝卜种子");
        } else if (seedRandom < CARROT_PROBABILITY + STRAWBERRY_PROBABILITY) {
            cropType = CropType.STRAWBERRY; // 草莓 30%
            System.out.println("选择草莓种子");
        } else {
            cropType = CropType.POTATO; // 土豆 20%
            System.out.println("选择土豆种子");
        }
        
        // 根据概率确定数量
        double quantityRandom = random.nextDouble();
        int quantity;
        System.out.println("数量随机数: " + quantityRandom);
        
        if (quantityRandom < ONE_SEED_PROBABILITY) {
            quantity = 1; // 70%
            System.out.println("数量: 1");
        } else if (quantityRandom < ONE_SEED_PROBABILITY + TWO_SEED_PROBABILITY) {
            quantity = 2; // 25%
            System.out.println("数量: 2");
        } else {
            quantity = 3; // 5%
            System.out.println("数量: 3");
        }
        
        return new LotteryResult(cropType, quantity);
    }
    
    private String getCropTypeName(CropType cropType) {
        switch (cropType) {
            case CARROT: return "胡萝卜";
            case STRAWBERRY: return "草莓";
            case POTATO: return "土豆";
            default: return "未知";
        }
    }
    
    /**
     * 抽奖结果类
     */
    private static class LotteryResult {
        final CropType cropType;
        final int quantity;
        
        LotteryResult(CropType cropType, int quantity) {
            this.cropType = cropType;
            this.quantity = quantity;
        }
    }
}
