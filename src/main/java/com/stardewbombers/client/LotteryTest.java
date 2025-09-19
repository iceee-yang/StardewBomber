package com.stardewbombers.client;

import com.stardewbombers.farm.Farm.CropType;
import com.stardewbombers.farm.SimpleFarmService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 简单的抽奖测试程序
 */
public class LotteryTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("抽奖测试");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label resultLabel = new Label("点击按钮开始抽奖");
        resultLabel.setStyle("-fx-font-size: 16px;");
        
        Button testBtn = new Button("测试抽奖");
        testBtn.setOnAction(e -> {
            try {
                // 使用单机版农场服务
                com.stardewbombers.farm.SinglePlayerFarmService farmService = com.stardewbombers.farm.FarmView.getGlobalFarmService();
                
                // 执行抽奖逻辑
                LotteryResult result = calculateLotteryResult();
                
                // 更新农场库存 - 直接操作全局数据
                farmService.addSeed(result.cropType, result.quantity);
                
                // 显示结果
                String resultText = String.format("获得 %d 个 %s种子！", 
                    result.quantity, getCropTypeName(result.cropType));
                resultLabel.setText(resultText);
                
                // 显示库存
                int carrotSeeds = farmService.getSeedCount(CropType.CARROT);
                int strawberrySeeds = farmService.getSeedCount(CropType.STRAWBERRY);
                int potatoSeeds = farmService.getSeedCount(CropType.POTATO);
                
                System.out.println("抽奖结果: " + resultText);
                System.out.println("当前库存:");
                System.out.println("胡萝卜种子: " + carrotSeeds);
                System.out.println("草莓种子: " + strawberrySeeds);
                System.out.println("土豆种子: " + potatoSeeds);
            } catch (Exception ex) {
                System.err.println("抽奖过程中出错: " + ex.getMessage());
                ex.printStackTrace();
                resultLabel.setText("抽奖失败，请重试");
            }
        });
        
        root.getChildren().addAll(titleLabel, resultLabel, testBtn);
        
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("抽奖测试");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private LotteryResult calculateLotteryResult() {
        java.util.Random random = new java.util.Random();
        
        // 种子概率
        double seedRandom = random.nextDouble();
        CropType cropType;
        
        if (seedRandom < 0.5) {
            cropType = CropType.CARROT; // 胡萝卜 50%
        } else if (seedRandom < 0.8) {
            cropType = CropType.STRAWBERRY; // 草莓 30%
        } else {
            cropType = CropType.POTATO; // 土豆 20%
        }
        
        // 数量概率
        double quantityRandom = random.nextDouble();
        int quantity;
        if (quantityRandom < 0.7) {
            quantity = 1; // 70%
        } else if (quantityRandom < 0.95) {
            quantity = 2; // 25%
        } else {
            quantity = 3; // 5%
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
    
    private static class LotteryResult {
        final CropType cropType;
        final int quantity;
        
        LotteryResult(CropType cropType, int quantity) {
            this.cropType = cropType;
            this.quantity = quantity;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
