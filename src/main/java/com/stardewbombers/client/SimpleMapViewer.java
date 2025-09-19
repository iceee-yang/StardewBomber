package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.util.SimpleMapLoader;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 简单的地图查看器
 * 使用JavaFX显示地图
 */
public class SimpleMapViewer extends Application {
    
    private static final int TILE_SIZE = 30; // 每个方块的像素大小
    private static final int SCALE = 2; // 缩放倍数
    
    @Override
    public void start(Stage primaryStage) {
        // 初始化纹理
        TextureManager.initialize(40, 40);

        // 加载地图
        GameMap gameMap = SimpleMapLoader.createFarmMap();
        
        // 创建根节点
        Group root = new Group();
        
        // 创建地图背景
        Rectangle background = new Rectangle(
            gameMap.getWidth() * TILE_SIZE * SCALE,
            gameMap.getHeight() * TILE_SIZE * SCALE
        );
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        root.getChildren().add(background);
        
        // 生成地图方块
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                // 先渲染地板（若有floor.png则使用图片，否则使用灰色占位）
                Image floorImage = TextureManager.getImage(BlockType.FLOOR);
                if (floorImage != null) {
                    ImageView floorView = new ImageView(floorImage);
                    floorView.setFitWidth(TILE_SIZE * SCALE);
                    floorView.setFitHeight(TILE_SIZE * SCALE);
                    floorView.setPreserveRatio(false);
                    floorView.setX(x * TILE_SIZE * SCALE);
                    floorView.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(floorView);
                } else {
                    Rectangle tileBG = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
                    tileBG.setFill(Color.web("#D9D9D9"));
                    tileBG.setStroke(Color.color(0,0,0,0.15));
                    tileBG.setX(x * TILE_SIZE * SCALE);
                    tileBG.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(tileBG);
                }

                Block block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != BlockType.FLOOR) {
                    ImageView tile = createTileImage(block.getType());
                    tile.setX(x * TILE_SIZE * SCALE);
                    tile.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(tile);
                }
            }
        }
        
        // 添加网格线
        addGridLines(root, gameMap);
        
        // 添加信息文本
        addInfoText(root, gameMap);
        
        // 创建场景
        Scene scene = new Scene(root, 
            gameMap.getWidth() * TILE_SIZE * SCALE + 200, 
            gameMap.getHeight() * TILE_SIZE * SCALE + 100);
        
        primaryStage.setTitle("StardewBombers - 地图查看器");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private ImageView createTileImage(BlockType type) {
        Image image = TextureManager.getImage(type);
        ImageView view = new ImageView();
        if (image != null) {
            view.setImage(image);
            view.setFitWidth(TILE_SIZE * SCALE);
            view.setFitHeight(TILE_SIZE * SCALE);
            view.setPreserveRatio(false);
            return view;
        }
        // 回退：若无纹理，使用灰色占位
        Rectangle rect = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
        rect.setFill(Color.GRAY);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1);
        Group group = new Group(rect);
        // 用快照转换为ImageView，简化层级
        Image snapshot = group.snapshot(null, null);
        ImageView fallback = new ImageView(snapshot);
        fallback.setFitWidth(TILE_SIZE * SCALE);
        fallback.setFitHeight(TILE_SIZE * SCALE);
        return fallback;
    }
    
    private void addGridLines(Group root, GameMap gameMap) {
        // 垂直线
        for (int x = 0; x <= gameMap.getWidth(); x++) {
            Rectangle line = new Rectangle(1, gameMap.getHeight() * TILE_SIZE * SCALE);
            line.setFill(Color.BLACK);
            line.setX(x * TILE_SIZE * SCALE);
            line.setY(0);
            root.getChildren().add(line);
        }
        
        // 水平线
        for (int y = 0; y <= gameMap.getHeight(); y++) {
            Rectangle line = new Rectangle(gameMap.getWidth() * TILE_SIZE * SCALE, 1);
            line.setFill(Color.BLACK);
            line.setX(0);
            line.setY(y * TILE_SIZE * SCALE);
            root.getChildren().add(line);
        }
    }
    
    private void addInfoText(Group root, GameMap gameMap) {
        int startX = gameMap.getWidth() * TILE_SIZE * SCALE + 10;
        int y = 20;
        
        Text title = new Text(startX, y, "地图信息:");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.getChildren().add(title);
        
        y += 30;
        Text sizeText = new Text(startX, y, "大小: " + gameMap.getWidth() + "x" + gameMap.getHeight());
        root.getChildren().add(sizeText);
        
        y += 25;
        Text tileSizeText = new Text(startX, y, "方块大小: " + gameMap.getTileWidth() + "x" + gameMap.getTileHeight());
        root.getChildren().add(tileSizeText);
        
        y += 25;
        Text destructibleText = new Text(startX, y, "可破坏方块: " + gameMap.getDestructibleBlocks().size());
        root.getChildren().add(destructibleText);
        
        y += 40;
        Text legendTitle = new Text(startX, y, "图例:");
        legendTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        root.getChildren().add(legendTitle);
        
        y += 30;
        addLegendItem(root, startX, y, Color.GREEN, "灌木 (可破坏)");
        y += 25;
        addLegendItem(root, startX, y, Color.ORANGE, "南瓜 (可破坏, 有奖励)");
        y += 25;
        addLegendItem(root, startX, y, Color.LIGHTGREEN, "瓜 (可破坏, 有奖励)");
        y += 25;
        addLegendItem(root, startX, y, Color.BROWN, "木桩 (不可破坏)");
        y += 25;
        addLegendItem(root, startX, y, Color.DARKRED, "柜子1/2 (可破坏, 有奖励)");
        y += 25;
        addLegendItem(root, startX, y, Color.DARKGOLDENROD, "桌子/凳子/椅子 (可破坏, 无奖励)");
        y += 25;
        addLegendItem(root, startX, y, Color.RED, "壁炉1/2 (不可破坏)");
        y += 25;
        addLegendItem(root, startX, y, Color.ORANGE, "地毯 (可通行)");
    }
    
    private void addLegendItem(Group root, int x, int y, Color color, String text) {
        Rectangle colorRect = new Rectangle(15, 15);
        colorRect.setFill(color);
        colorRect.setStroke(Color.BLACK);
        colorRect.setX(x);
        colorRect.setY(y - 12);
        root.getChildren().add(colorRect);
        
        Text textNode = new Text(x + 20, y, text);
        root.getChildren().add(textNode);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
