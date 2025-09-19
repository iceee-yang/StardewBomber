package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.component.MovementComponent;
import com.stardewbombers.component.BombComponent;
import com.stardewbombers.shared.util.SimpleMapLoader;
import com.stardewbombers.shared.util.MapLoader;
import com.stardewbombers.shared.util.GameConfig;
import com.stardewbombers.shared.game.GameManager;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 玩家和炸弹系统可视化测试程序（无音乐版本）
 */
public class PlayerBombVisualTestNoMusic extends Application {
    
    private static final int TILE_SIZE = 40;
    private static final int SCALE = 1;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    // 游戏对象
    private GameMap gameMap;
    private GameManager gameManager;
    private List<PlayerComponent> players;
    private Map<String, ImageView> playerVisuals;
    private Map<String, List<javafx.scene.Node>> bombVisuals;
    private List<javafx.scene.Node> explosionVisuals;
    private Map<String, javafx.scene.Node> blockVisuals;
    
    // UI组件
    private Group root;
    private Label statusLabel;
    
    // 游戏状态
    private long lastUpdateTime;
    private boolean gameRunning;
    private String currentMapName = "cave_map";
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("开始初始化游戏（无音乐版本）...");
            initializeGame();
            
            System.out.println("创建UI界面...");
            createUI();
            
            System.out.println("设置场景...");
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.setFill(Color.LIGHTGRAY);
            
            setupControls(scene);
            
            System.out.println("设置窗口属性...");
            primaryStage.setTitle("StardewBombers - 玩家和炸弹系统测试（无音乐版）");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("游戏窗口关闭，清理资源...");
                System.exit(0);
            });
            
            System.out.println("显示窗口...");
            primaryStage.show();
            
            System.out.println("启动游戏循环...");
            startGameLoop();
            
            System.out.println("程序初始化完成！");
        } catch (Exception e) {
            System.err.println("程序启动过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeGame() {
        // 加载地图
        try {
            gameMap = MapLoader.loadMap(currentMapName);
            System.out.println("成功加载Tiled地图: " + currentMapName + ".json");
        } catch (Exception e) {
            System.out.println("加载Tiled地图失败，使用默认地图: " + e.getMessage());
            gameMap = SimpleMapLoader.createFarmMap();
        }
        
        // 初始化游戏管理器
        gameManager = new GameManager();
        gameManager.setGameMap(gameMap, SCALE);
        
        // 创建玩家
        players = new ArrayList<>();
        playerVisuals = new HashMap<>();
        bombVisuals = new HashMap<>();
        explosionVisuals = new ArrayList<>();
        blockVisuals = new HashMap<>();
        
        // 创建两个测试玩家
        createPlayer("player1", new Point2D(3 * TILE_SIZE + TILE_SIZE/2, 3 * TILE_SIZE + TILE_SIZE/2), Color.BLUE);
        createPlayer("player2", new Point2D(11 * TILE_SIZE + TILE_SIZE/2, 9 * TILE_SIZE + TILE_SIZE/2), Color.RED);
        
        lastUpdateTime = System.currentTimeMillis();
        gameRunning = true;
    }
    
    private void createPlayer(String id, Point2D position, Color color) {
        Player player = new Player(id, position);
        MovementComponent movement = new MovementComponent(position, TILE_SIZE * SCALE);
        BombComponent bombs = new BombComponent(id, 1, 2);
        PlayerComponent playerComponent = new PlayerComponent(player, movement, bombs);
        
        movement.setItemManager(gameMap.getItemManager());
        players.add(playerComponent);
        gameManager.addPlayer(playerComponent);
        
        // 创建简单的玩家视觉表示
        Rectangle playerVisual = new Rectangle(16, 32);
        playerVisual.setFill(color);
        playerVisual.setStroke(Color.BLACK);
        playerVisual.setStrokeWidth(2);
        playerVisual.setX(position.getX() - 8);
        playerVisual.setY(position.getY() - 16);
        
        playerVisuals.put(id, new ImageView());
        bombVisuals.put(id, new ArrayList<>());
    }
    
    private void createUI() {
        root = new Group();
        
        // 绘制简单的地图背景
        Rectangle background = new Rectangle(
            gameMap.getWidth() * TILE_SIZE * SCALE,
            gameMap.getHeight() * TILE_SIZE * SCALE
        );
        background.setFill(Color.web("#D9D9D9"));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(2);
        root.getChildren().add(background);
        
        // 创建状态标签
        statusLabel = new Label("游戏状态: 运行中（无音乐版本）");
        statusLabel.setFont(new Font(14));
        statusLabel.setLayoutX(10);
        statusLabel.setLayoutY(10);
        statusLabel.setTextFill(Color.BLUE);
        root.getChildren().add(statusLabel);
    }
    
    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            
            // 玩家1控制 (WASD)
            if (key == KeyCode.W) {
                players.get(0).moveUp();
            } else if (key == KeyCode.S) {
                players.get(0).moveDown();
            } else if (key == KeyCode.A) {
                players.get(0).moveLeft();
            } else if (key == KeyCode.D) {
                players.get(0).moveRight();
            } else if (key == KeyCode.SPACE) {
                players.get(0).placeBomb(System.currentTimeMillis());
            }
            
            // 玩家2控制 (方向键)
            if (players.size() > 1) {
                if (key == KeyCode.UP) {
                    players.get(1).moveUp();
                } else if (key == KeyCode.DOWN) {
                    players.get(1).moveDown();
                } else if (key == KeyCode.LEFT) {
                    players.get(1).moveLeft();
                } else if (key == KeyCode.RIGHT) {
                    players.get(1).moveRight();
                } else if (key == KeyCode.ENTER) {
                    players.get(1).placeBomb(System.currentTimeMillis());
                }
            }
        });
    }
    
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (gameRunning) {
                        updateGame();
                        updateVisuals();
                        updateUI();
                    }
                } catch (Exception e) {
                    System.err.println("游戏循环异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        gameLoop.start();
    }
    
    private void updateGame() {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        
        // 更新所有玩家
        for (PlayerComponent playerComponent : players) {
            playerComponent.getMovement().update();
            List<Bomb> explodedBombs = playerComponent.getBombs().tick(currentTime);
            for (Bomb bomb : explodedBombs) {
                handleBombExplosion(bomb, currentTime);
            }
        }
        
        gameManager.update(currentTime);
    }
    
    private void handleBombExplosion(Bomb bomb, long currentTime) {
        System.out.println("炸弹爆炸: " + bomb.getOwnerId());
        // 简单的爆炸效果
        Circle explosion = new Circle();
        explosion.setCenterX(bomb.getWorldX());
        explosion.setCenterY(bomb.getWorldY());
        explosion.setRadius(TILE_SIZE);
        explosion.setFill(Color.YELLOW);
        explosion.setOpacity(0.8);
        
        explosionVisuals.add(explosion);
        root.getChildren().add(explosion);
        
        // 1秒后清除爆炸效果
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1000));
        pause.setOnFinished(event -> {
            root.getChildren().remove(explosion);
            explosionVisuals.remove(explosion);
        });
        pause.play();
    }
    
    private void updateVisuals() {
        // 更新玩家位置
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            Point2D position = playerComponent.getPlayer().getPosition();
            
            // 这里可以更新玩家视觉位置
        }
    }
    
    private void updateUI() {
        // 更新状态
        StringBuilder statusText = new StringBuilder();
        statusText.append("游戏状态: 运行中（无音乐版本）");
        
        for (int i = 0; i < players.size(); i++) {
            PlayerComponent playerComponent = players.get(i);
            Player player = playerComponent.getPlayer();
            statusText.append(String.format(" | 玩家%d: %d/%d HP %s", 
                i + 1, player.getHealth(), GameConfig.PLAYER_MAX_HP, 
                player.isAlive() ? "存活" : "死亡"));
        }
        
        statusLabel.setText(statusText.toString());
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("启动PlayerBombVisualTestNoMusic...");
            launch(args);
        } catch (Exception e) {
            System.err.println("程序启动异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
