package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.component.MovementComponent;
import com.stardewbombers.component.BombComponent;
import com.stardewbombers.shared.util.SimpleMapLoader;
import com.stardewbombers.shared.util.MapLoader;
import com.stardewbombers.shared.game.GameManager;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 玩家和炸弹系统可视化测试程序
 * 在地图中显示玩家移动、炸弹放置和爆炸效果
 */
public class PlayerBombVisualTest extends Application {
    
    private static final int TILE_SIZE = 40;
    private static final int SCALE = 1; // 减小缩放，从2改为1
    // 窗口大小调整为能完整显示地图：15格×40像素=600，13格×40像素=520
    private static final int WINDOW_WIDTH = 900;    // 600 + 300边距（为右侧玩家信息面板留空间）
    private static final int WINDOW_HEIGHT = 600;   // 520 + 80边距（为下方状态栏留空间）
    
    // 游戏对象
    private GameMap gameMap;
    private GameManager gameManager;
    private List<PlayerComponent> players;
    private Map<String, ImageView> playerVisuals;
    private Map<String, List<javafx.scene.Node>> bombVisuals;
    private List<javafx.scene.Node> explosionVisuals;
    private Map<String, javafx.scene.Node> blockVisuals;
    private Map<String, PlayerAnimationManager> playerAnimationManagers;
    private List<javafx.scene.Node> itemVisuals; // 道具视觉列表
    private java.util.Set<String> processedExplosions; // 已处理的爆炸集合
    private Map<String, PlayerAnimationManager.CharacterType> playerCharacters; // 玩家角色映射
    
    // UI组件
    private Group root;
    private UIManager uiManager;
    
    // 游戏状态
    private long lastUpdateTime;
    private boolean gameRunning;
    private String currentMapName;
    private com.stardewbombers.model.Player currentUser; // 当前登录的用户
    
    // 可用地图列表
    private static final String[] AVAILABLE_MAPS = {"cave_map", "home_map", "farm_map"};
    
    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, null, null, null);
    }
    
    public void start(Stage primaryStage, String selectedMap, Map<String, String> selectedCharacters) {
        start(primaryStage, selectedMap, selectedCharacters, null);
    }
    
    public void start(Stage primaryStage, String selectedMap, Map<String, String> selectedCharacters, com.stardewbombers.model.Player user) {
        try {
            System.out.println("开始初始化游戏...");
            // 设置当前用户
            this.currentUser = user;
            // 初始化游戏
            initializeGame(selectedMap, selectedCharacters);
            
            System.out.println("创建UI界面...");
            // 创建UI
            createUI();
            
            System.out.println("设置场景...");
            // 设置场景
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.setFill(Color.web("#2C3E50")); // 深色背景，让地图更明显
            
            // 设置键盘控制
            setupControls(scene);
            
            System.out.println("设置窗口属性...");
            // 设置窗口
            primaryStage.setTitle("StardewBombers - 玩家和炸弹系统可视化测试");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            
            // 设置窗口关闭事件，确保清理资源
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("游戏窗口关闭，清理资源...");
                try {
                    MusicManager.cleanup();
                } catch (Exception e) {
                    System.err.println("清理音乐资源时出错: " + e.getMessage());
                }
                System.exit(0);
            });
            
            System.out.println("显示窗口...");
            primaryStage.show();
            
            System.out.println("启动背景音乐...");
            // 启动背景音乐（暂时禁用以测试）
            try {
                MusicManager.startBackgroundMusic();
            } catch (Exception e) {
                System.err.println("启动背景音乐时出错: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("启动游戏循环...");
            // 启动游戏循环
            startGameLoop();
            
            System.out.println("程序初始化完成！");
        } catch (Exception e) {
            System.err.println("程序启动过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeGame() {
        initializeGame(null, null);
    }
    
    private void initializeGame(String selectedMap, Map<String, String> selectedCharacters) {
        // 初始化音乐管理器
        MusicManager.initialize();
        
        // 选择地图
        if (selectedMap != null) {
            currentMapName = selectedMap;
            System.out.println("使用选择的地图: " + currentMapName);
        } else {
            selectRandomMap();
        }
        
        // 加载Tiled地图
        try {
            gameMap = MapLoader.loadMap(currentMapName); // 使用你的Tiled地图
            System.out.println("成功加载Tiled地图: " + currentMapName + ".json");
        } catch (Exception e) {
            System.out.println("加载Tiled地图失败，使用默认地图: " + e.getMessage());
            gameMap = SimpleMapLoader.createFarmMap(); // 回退到默认地图
        }
        
        // 初始化游戏管理器
        gameManager = new GameManager();
        gameManager.setGameMap(gameMap, SCALE); // 使用缩放因子
        
        // 设置游戏结束回调
        gameManager.addGameEndCallback(this::handleGameEnd);
        
        // 创建玩家
        players = new ArrayList<>();
        playerVisuals = new HashMap<>();
        bombVisuals = new HashMap<>();
        explosionVisuals = new ArrayList<>();
        blockVisuals = new HashMap<>();
        playerAnimationManagers = new HashMap<>();
        itemVisuals = new ArrayList<>();
        processedExplosions = new java.util.HashSet<>();
        playerCharacters = new HashMap<>();
        
        // 选择角色
        if (selectedCharacters != null) {
            selectCharacters(selectedCharacters);
        } else {
            selectRandomCharacters();
        }
        
        // 创建三个测试玩家，位置适应地图大小
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        System.out.println("地图大小: " + mapWidth + "x" + mapHeight);
        
        // 玩家1：第3排第3列格子的正中央（确保在地图范围内）
        createPlayer("player1", new Point2D( (3 * TILE_SIZE + TILE_SIZE/2), (3 * TILE_SIZE + TILE_SIZE/2)), Color.BLUE);
        // 玩家2：第9排第11列格子的正中央（确保在地图范围内）
        createPlayer("player2", new Point2D( (11 * TILE_SIZE + TILE_SIZE/2),  (9 * TILE_SIZE + TILE_SIZE/2)), Color.RED);
        // 玩家3：随机生成位置，避开solid元素和其他玩家
        Point2D player3Position = findValidSpawnPosition();
        createPlayer("player3", player3Position, Color.GREEN);
        
        lastUpdateTime = System.currentTimeMillis();
        gameRunning = true;
    }
    
    /**
     * 随机选择地图
     */
    private void selectRandomMap() {
        int randomIndex = (int) (Math.random() * AVAILABLE_MAPS.length);
        currentMapName = AVAILABLE_MAPS[randomIndex];
        System.out.println("随机选择地图: " + currentMapName);
    }
    
    /**
     * 使用选择的人物分配给玩家
     */
    private void selectCharacters(Map<String, String> selectedCharacters) {
        // 将选择的人物名称转换为CharacterType
        for (Map.Entry<String, String> entry : selectedCharacters.entrySet()) {
            String playerId = entry.getKey();
            String characterName = entry.getValue();
            
            PlayerAnimationManager.CharacterType characterType = convertToCharacterType(characterName);
            playerCharacters.put(playerId, characterType);
            System.out.println("玩家" + playerId + " 分配角色: " + characterType);
        }
    }
    
    /**
     * 将人物名称转换为CharacterType枚举
     */
    private PlayerAnimationManager.CharacterType convertToCharacterType(String characterName) {
        switch (characterName) {
            case "Abigail":
                return PlayerAnimationManager.CharacterType.ABIGAIL;
            case "Alex":
                return PlayerAnimationManager.CharacterType.ALEX;
            case "Haley":
                return PlayerAnimationManager.CharacterType.HALEY;
            case "Lewis":
                return PlayerAnimationManager.CharacterType.LEWIS;
            default:
                return PlayerAnimationManager.CharacterType.ABIGAIL; // 默认
        }
    }
    
    /**
     * 随机选择3个不重复的角色分配给3个玩家
     */
    private void selectRandomCharacters() {
        // 获取所有可用的角色类型
        PlayerAnimationManager.CharacterType[] allCharacters = PlayerAnimationManager.CharacterType.values();
        
        // 创建角色列表并打乱顺序
        java.util.List<PlayerAnimationManager.CharacterType> characterList = new java.util.ArrayList<>();
        for (PlayerAnimationManager.CharacterType character : allCharacters) {
            characterList.add(character);
        }
        java.util.Collections.shuffle(characterList);
        
        // 为3个玩家分配前3个角色
        String[] playerIds = {"player1", "player2", "player3"};
        for (int i = 0; i < 3; i++) {
            playerCharacters.put(playerIds[i], characterList.get(i));
            System.out.println("玩家" + (i + 1) + " 分配角色: " + characterList.get(i));
        }
    }
    
    /**
     * 寻找有效的玩家生成位置
     * 避开solid元素和其他玩家一个格子以内的位置
     */
    private Point2D findValidSpawnPosition() {
        int mapWidth = gameMap.getWidth();
        int mapHeight = gameMap.getHeight();
        int maxAttempts = 100; // 最大尝试次数
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // 随机生成位置
            int randomX = (int) (Math.random() * mapWidth);
            int randomY = (int) (Math.random() * mapHeight);
            
            // 检查该位置是否有效
            if (isValidSpawnPosition(randomX, randomY)) {
                // 转换为世界坐标（格子中心）
                double worldX = randomX * TILE_SIZE + TILE_SIZE / 2.0;
                double worldY = randomY * TILE_SIZE + TILE_SIZE / 2.0;
                System.out.println("找到有效生成位置: (" + randomX + ", " + randomY + ") -> 世界坐标: (" + worldX + ", " + worldY + ")");
                return new Point2D(worldX, worldY);
            }
        }
        
        // 如果找不到随机位置，使用备用位置
        System.out.println("无法找到随机生成位置，使用备用位置");
        return new Point2D(7 * TILE_SIZE + TILE_SIZE/2, 7 * TILE_SIZE + TILE_SIZE/2);
    }
    
    /**
     * 检查指定位置是否可以作为玩家生成位置
     */
    private boolean isValidSpawnPosition(int gridX, int gridY) {
        // 检查是否在地图范围内
        if (gridX < 0 || gridX >= gameMap.getWidth() || gridY < 0 || gridY >= gameMap.getHeight()) {
            return false;
        }
        
        // 检查该位置是否有solid元素
        var block = gameMap.getBlock(gridX, gridY);
        if (block != null && block.getType().isSolid()) {
            return false;
        }
        
        // 检查是否距离其他玩家一个格子以内
        for (PlayerComponent playerComponent : players) {
            Point2D playerPos = playerComponent.getPlayer().getPosition();
            int playerGridX = (int) (playerPos.getX() / TILE_SIZE);
            int playerGridY = (int) (playerPos.getY() / TILE_SIZE);
            
            // 计算距离（曼哈顿距离）
            int distance = Math.abs(gridX - playerGridX) + Math.abs(gridY - playerGridY);
            if (distance <= 1) {
                return false;
            }
        }
        
        return true;
    }
    
    private void createPlayer(String id, Point2D position, Color color) {
        // 创建玩家实体和组件
        com.stardewbombers.shared.entity.Player player = new com.stardewbombers.shared.entity.Player(id, position);
        MovementComponent movement = new MovementComponent(position, TILE_SIZE * SCALE); // 平滑移动一个格子的距离
        BombComponent bombs = new BombComponent(id, 1, 2);
        PlayerComponent playerComponent = new PlayerComponent(player, movement, bombs);
        
        // 设置道具管理器
        movement.setItemManager(gameMap.getItemManager());
        
        // 设置炸弹组件的地图引用
        bombs.setGameMap(gameMap);
        
        players.add(playerComponent);
        
        // 将玩家添加到游戏管理器
        gameManager.addPlayer(playerComponent);
        
        // 创建玩家视觉表示 - 使用角色动画
        // 为每个玩家创建独立的动画管理器
        PlayerAnimationManager animationManager = new PlayerAnimationManager();
        
        // 设置玩家角色
        PlayerAnimationManager.CharacterType assignedCharacter = playerCharacters.get(id);
        if (assignedCharacter != null) {
            animationManager.setCharacter(assignedCharacter);
            System.out.println("为玩家 " + id + " 设置角色: " + assignedCharacter);
        }
        
        playerAnimationManagers.put(id, animationManager);
        
        // 创建玩家视觉表示 - 使用角色动画
        ImageView playerVisual = new ImageView(animationManager.getCurrentFrameView().getImage());
        playerVisual.setFitWidth(16); // 设置帧宽度
        playerVisual.setFitHeight(32); // 设置帧高度
        playerVisual.setPreserveRatio(false);
        
        // 设置当前帧的viewport
        int sourceX = 0; // 第一帧
        int sourceY = 0; // 向下方向
        playerVisual.setViewport(new javafx.geometry.Rectangle2D(
            sourceX, sourceY, 16, 32
        ));
        
        playerVisual.setX(position.getX() - 8); // 调整位置，使角色居中
        playerVisual.setY(position.getY() - 16); // 调整位置，使角色底部对齐
        
        playerVisuals.put(id, playerVisual);
        bombVisuals.put(id, new ArrayList<>());
    }
    
    private void createUI() {
        root = new Group();
        
        // 先创建UIManager来管理所有UI组件（背景会在最底层）
        uiManager = new UIManager(root);
        
        // 设置游戏暂停回调
        uiManager.setGamePauseCallback(() -> {
            gameRunning = !gameRunning;
            System.out.println("游戏状态切换: " + (gameRunning ? "运行中" : "暂停"));
        });
        
        // 更新用户名显示
        System.out.println("尝试更新用户名显示，用户信息: " + (currentUser != null ? currentUser.getNickname() : "null"));
        if (currentUser != null && currentUser.getNickname() != null) {
            uiManager.updateUsername(currentUser.getNickname());
            System.out.println("用户名显示已更新为: " + currentUser.getNickname());
        } else {
            System.out.println("用户信息为空，使用默认显示");
        }
        
        // 绘制地图（不绘制地图背景，使用UIManager的背景）
        drawMapWithoutBackground();
        
        // 添加玩家视觉
        for (ImageView playerVisual : playerVisuals.values()) {
            root.getChildren().add(playerVisual);
        }
    }
    
    private void drawMapWithoutBackground() {
        // 初始化纹理管理器
        TextureManager.initialize(TILE_SIZE, TILE_SIZE);
        
        System.out.println("地图绘制完成，尺寸: " + (gameMap.getWidth() * TILE_SIZE * SCALE) + "x" + (gameMap.getHeight() * TILE_SIZE * SCALE));
        
        // 绘制地图方块（不绘制背景，使用UIManager的背景）
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                // 先绘制地板纹理
                Image floorImage = TextureManager.getImage(com.stardewbombers.shared.enums.BlockType.FLOOR);
                if (floorImage != null) {
                    ImageView floorView = new ImageView(floorImage);
                    floorView.setFitWidth(TILE_SIZE * SCALE);
                    floorView.setFitHeight(TILE_SIZE * SCALE);
                    floorView.setPreserveRatio(false);
                    floorView.setX(x * TILE_SIZE * SCALE);
                    floorView.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(floorView);
                }
                
                // 绘制其他方块
                var block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != com.stardewbombers.shared.enums.BlockType.FLOOR) {
                    String blockKey = x + "," + y;
                    Image blockImage = TextureManager.getImage(block.getType());
                    javafx.scene.Node blockVisual;
                    
                    if (blockImage != null) {
                        // 使用纹理图片
                        ImageView tileView = new ImageView(blockImage);
                        tileView.setFitWidth(TILE_SIZE * SCALE);
                        tileView.setFitHeight(TILE_SIZE * SCALE);
                        tileView.setPreserveRatio(false);
                        tileView.setX(x * TILE_SIZE * SCALE);
                        tileView.setY(y * TILE_SIZE * SCALE);
                        blockVisual = tileView;
                    } else {
                        // 回退到颜色方块
                        Rectangle tile = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
                        tile.setX(x * TILE_SIZE * SCALE);
                        tile.setY(y * TILE_SIZE * SCALE);
                        tile.setFill(getBlockColor(block.getType()));
                        tile.setStroke(Color.BLACK);
                        tile.setStrokeWidth(1);
                        blockVisual = tile;
                    }
                    
                    root.getChildren().add(blockVisual);
                    blockVisuals.put(blockKey, blockVisual);
                }
            }
        }
        
        // 绘制网格线
        drawGridLines();
    }
    
    private void drawMap() {
        // 初始化纹理管理器
        TextureManager.initialize(TILE_SIZE, TILE_SIZE);
        
        // 绘制地图背景
        Rectangle background = new Rectangle(
            gameMap.getWidth() * TILE_SIZE * SCALE,
            gameMap.getHeight() * TILE_SIZE * SCALE
        );
        background.setFill(Color.web("#34495E")); // 更明显的背景色
        background.setStroke(Color.web("#2C3E50")); // 深色边框
        background.setStrokeWidth(3);
        root.getChildren().add(background);
        
        System.out.println("地图背景绘制完成，尺寸: " + (gameMap.getWidth() * TILE_SIZE * SCALE) + "x" + (gameMap.getHeight() * TILE_SIZE * SCALE));
        
        // 绘制地图方块
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                // 先绘制地板纹理
                Image floorImage = TextureManager.getImage(com.stardewbombers.shared.enums.BlockType.FLOOR);
                if (floorImage != null) {
                    ImageView floorView = new ImageView(floorImage);
                    floorView.setFitWidth(TILE_SIZE * SCALE);
                    floorView.setFitHeight(TILE_SIZE * SCALE);
                    floorView.setPreserveRatio(false);
                    floorView.setX(x * TILE_SIZE * SCALE);
                    floorView.setY(y * TILE_SIZE * SCALE);
                    root.getChildren().add(floorView);
                }
                
                // 绘制其他方块
                var block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != com.stardewbombers.shared.enums.BlockType.FLOOR) {
                    String blockKey = x + "," + y;
                    Image blockImage = TextureManager.getImage(block.getType());
                    javafx.scene.Node blockVisual;
                    
                    if (blockImage != null) {
                        // 使用纹理图片
                        ImageView tileView = new ImageView(blockImage);
                        tileView.setFitWidth(TILE_SIZE * SCALE);
                        tileView.setFitHeight(TILE_SIZE * SCALE);
                        tileView.setPreserveRatio(false);
                        tileView.setX(x * TILE_SIZE * SCALE);
                        tileView.setY(y * TILE_SIZE * SCALE);
                        blockVisual = tileView;
                    } else {
                        // 回退到颜色方块
                        Rectangle tile = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
                        tile.setX(x * TILE_SIZE * SCALE);
                        tile.setY(y * TILE_SIZE * SCALE);
                        tile.setFill(getBlockColor(block.getType()));
                        tile.setStroke(Color.BLACK);
                        tile.setStrokeWidth(1);
                        blockVisual = tile;
                    }
                    
                    root.getChildren().add(blockVisual);
                    blockVisuals.put(blockKey, blockVisual);
                }
            }
        }
        
        // 绘制网格线
        drawGridLines();
    }
    
    private Color getBlockColor(com.stardewbombers.shared.enums.BlockType type) {
        switch (type) {
            case BUSHES: return Color.GREEN;
            case PUMPKIN: return Color.ORANGE;
            case MELON: return Color.LIGHTGREEN;
            case STUMP: return Color.BROWN;
            case CABINET1:
            case CABINET2: return Color.DARKRED;
            case TABLE:
            case STOOL:
            case CHAIR: return Color.DARKGOLDENROD;
            case FIREPLACE1:
            case FIREPLACE2: return Color.RED;
            case RUG: return Color.ORANGE;
            
            // 洞穴元素颜色
            case GHOST: return Color.WHITE;           // 幽灵 - 白色
            case SKELTON: return Color.LIGHTGRAY;     // 骷髅 - 浅灰色
            case MUMMY: return Color.BEIGE;           // 木乃伊 - 米色
            case DIRETFLOOR: return Color.SADDLEBROWN; // 泥土地板 - 马鞍棕色
            case BIXITE: return Color.DARKSLATEGRAY;  // 黑曜石 - 深石板灰
            case QUARTZ: return Color.LIGHTBLUE;      // 石英 - 浅蓝色
            case MUSHROOM1: return Color.RED;         // 蘑菇1 - 红色
            case MUSHROOM2: return Color.PURPLE;      // 蘑菇2 - 紫色
            
            default: return Color.GRAY;
        }
    }
    
    private void drawGridLines() {
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
    
    
    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            
            // 玩家1控制 (WASD)
            if (key == KeyCode.W) {
                players.get(0).moveUp();
                playerAnimationManagers.get("player1").setDirection(PlayerAnimationManager.Direction.UP);
            } else if (key == KeyCode.S) {
                players.get(0).moveDown();
                playerAnimationManagers.get("player1").setDirection(PlayerAnimationManager.Direction.DOWN);
            } else if (key == KeyCode.A) {
                players.get(0).moveLeft();
                playerAnimationManagers.get("player1").setDirection(PlayerAnimationManager.Direction.LEFT);
            } else if (key == KeyCode.D) {
                players.get(0).moveRight();
                playerAnimationManagers.get("player1").setDirection(PlayerAnimationManager.Direction.RIGHT);
            } else if (key == KeyCode.Q) {
                System.out.println("玩家1按下空格键，尝试放置炸弹");
                boolean success = players.get(0).placeBomb(System.currentTimeMillis());
                System.out.println("玩家1放置炸弹结果: " + success);
            }
            
            // 玩家2控制 (方向键)
            if (players.size() > 1) {
                if (key == KeyCode.UP) {
                    players.get(1).moveUp();
                    playerAnimationManagers.get("player2").setDirection(PlayerAnimationManager.Direction.UP);
                } else if (key == KeyCode.DOWN) {
                    players.get(1).moveDown();
                    playerAnimationManagers.get("player2").setDirection(PlayerAnimationManager.Direction.DOWN);
                } else if (key == KeyCode.LEFT) {
                    players.get(1).moveLeft();
                    playerAnimationManagers.get("player2").setDirection(PlayerAnimationManager.Direction.LEFT);
                } else if (key == KeyCode.RIGHT) {
                    players.get(1).moveRight();
                    playerAnimationManagers.get("player2").setDirection(PlayerAnimationManager.Direction.RIGHT);
                } else if (key == KeyCode.ENTER) {
                    System.out.println("玩家2按下回车键，尝试放置炸弹");
                    boolean success = players.get(1).placeBomb(System.currentTimeMillis());
                    System.out.println("玩家2放置炸弹结果: " + success);
                }
            }
            
            // 玩家3控制 (i/k/j/l/p)
            if (players.size() > 2) {
                if (key == KeyCode.I) {
                    players.get(2).moveUp();
                    playerAnimationManagers.get("player3").setDirection(PlayerAnimationManager.Direction.UP);
                } else if (key == KeyCode.K) {
                    players.get(2).moveDown();
                    playerAnimationManagers.get("player3").setDirection(PlayerAnimationManager.Direction.DOWN);
                } else if (key == KeyCode.J) {
                    players.get(2).moveLeft();
                    playerAnimationManagers.get("player3").setDirection(PlayerAnimationManager.Direction.LEFT);
                } else if (key == KeyCode.L) {
                    players.get(2).moveRight();
                    playerAnimationManagers.get("player3").setDirection(PlayerAnimationManager.Direction.RIGHT);
                } else if (key == KeyCode.SPACE) {
                    System.out.println("玩家3按下P键，尝试放置炸弹");
                    boolean success = players.get(2).placeBomb(System.currentTimeMillis());
                    System.out.println("玩家3放置炸弹结果: " + success);
                }
            }
            
            // 特殊控制
            if (key == KeyCode.R) {
                // 重置游戏
                resetGame();
            } else if (key == KeyCode.M) {
                // 切换音乐开关
                MusicManager.setMusicEnabled(!MusicManager.isMusicEnabled());
            } else if (key == KeyCode.N) {
                // 切换音效开关
                MusicManager.setSoundEnabled(!MusicManager.isSoundEnabled());
            } else if (key == KeyCode.PLUS || key == KeyCode.ADD) {
                // 增加音乐音量
                double currentVolume = MusicManager.getMusicVolume();
                MusicManager.setMusicVolume(Math.min(1.0, currentVolume + 0.1));
            } else if (key == KeyCode.MINUS || key == KeyCode.SUBTRACT) {
                // 减少音乐音量
                double currentVol = MusicManager.getMusicVolume();
                MusicManager.setMusicVolume(Math.max(0.0, currentVol - 0.1));
            } else if (key == KeyCode.EQUALS) {
                // 增加音效音量
                double currentSoundVol = MusicManager.getSoundVolume();
                MusicManager.setSoundVolume(Math.min(1.0, currentSoundVol + 0.1));
            } else if (key == KeyCode.BRACELEFT) {
                // 减少音效音量
                double currentSoundVol = MusicManager.getSoundVolume();
                MusicManager.setSoundVolume(Math.max(0.0, currentSoundVol - 0.1));
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
                    // 不要退出程序，继续运行
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
            // 更新移动组件
            playerComponent.getMovement().update();
        }
        
        // 使用deltaTime进行游戏逻辑更新（目前暂时不需要，但保留以备将来使用）
        if (deltaTime > 0.1) {
            // 如果帧率过低，可以在这里进行特殊处理
            System.out.println("帧率过低，deltaTime: " + deltaTime);
        }
        
        // 使用游戏管理器更新游戏状态（包括爆炸处理）
        gameManager.update(currentTime);
        
        // 处理爆炸视觉效果（从GameManager获取爆炸的炸弹）
        for (PlayerComponent playerComponent : players) {
            List<Bomb> explodedBombs = playerComponent.getBombs().getExplodedBombs();
            for (Bomb bomb : explodedBombs) {
                System.out.println("检测到炸弹爆炸，处理视觉效果: " + bomb.getOwnerId());
                handleBombExplosion(bomb, currentTime);
            }
            // 清理已处理的爆炸炸弹列表
            playerComponent.getBombs().clearExplodedBombs();
        }
        
        // 处理爆炸后的视觉效果更新
        List<ExplosionEvent> activeExplosions = gameManager.getActiveExplosions();
        if (!activeExplosions.isEmpty()) {
            updateMapVisuals();
        }
        
        // 更新道具视觉
        updateItemVisuals();
    }
    
    private void handleBombExplosion(Bomb bomb, long currentTime) {
        System.out.println("=== 炸弹爆炸视觉效果处理开始 ===");
        System.out.println("处理炸弹爆炸: " + bomb.getOwnerId() + " 网格位置: (" + bomb.getX() + ", " + bomb.getY() + ")");
        System.out.println("炸弹世界坐标: (" + bomb.getWorldX() + ", " + bomb.getWorldY() + ")");
        System.out.println("炸弹是否已被添加到爆炸列表: " + bomb.hasBeenAddedToExplodedList());
        
        // 检查炸弹是否已经被处理过视觉效果
        String bombKey = bomb.getOwnerId() + "_" + bomb.getX() + "_" + bomb.getY();
        if (processedExplosions.contains(bombKey)) {
            System.out.println("炸弹 " + bombKey + " 的视觉效果已经被处理过，跳过重复处理");
            return;
        }
        processedExplosions.add(bombKey);
        
        // 播放爆炸音效
        MusicManager.playExplosionSound();
        
        // 创建爆炸视觉效果
        // 直接使用炸弹对象来获取爆炸范围
        BombComponent bombComponent = null;
        for (PlayerComponent playerComponent : players) {
            if (playerComponent.getBombs().getOwnerId().equals(bomb.getOwnerId())) {
                bombComponent = playerComponent.getBombs();
                break;
            }
        }
        
        if (bombComponent != null) {
            // 创建爆炸特效，不管玩家是否存活
            List<Point2D> explosionRange = bombComponent.getExplosionRange(bomb);
            createExplosionVisual(explosionRange);
        }
    }
    
    private void updateMapVisuals() {
        System.out.println("=== 更新地图方块视觉 ===");
        // 遍历所有方块，检查哪些被破坏了
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                String blockKey = x + "," + y;
                Block block = gameMap.getBlock(x, y);
                
                if (block != null && block.isDestroyed()) {
                    // 如果方块被破坏了，移除其视觉节点
                    javafx.scene.Node blockVisual = blockVisuals.get(blockKey);
                    if (blockVisual != null) {
                        root.getChildren().remove(blockVisual);
                        blockVisuals.remove(blockKey);
                    }
                }
            }
        }
        System.out.println("=== 地图方块视觉更新完成 ===");
    }
    
    /**
     * 更新道具视觉显示
     */
    private void updateItemVisuals() {
        // 清除旧的道具视觉
        root.getChildren().removeAll(itemVisuals);
        itemVisuals.clear();
        
        // 获取所有活跃的道具
        List<com.stardewbombers.shared.entity.Item> activeItems = gameMap.getItemManager().getActiveItems();
        
        for (com.stardewbombers.shared.entity.Item item : activeItems) {
            // 创建道具视觉
            javafx.scene.Node itemVisual = createItemVisual(item);
            if (itemVisual != null) {
                itemVisuals.add(itemVisual);
                root.getChildren().add(itemVisual);
            }
        }
    }
    
    /**
     * 创建道具视觉表示
     */
    private javafx.scene.Node createItemVisual(com.stardewbombers.shared.entity.Item item) {
        try {
            // 使用TextureManager加载道具图像
            Image itemImage = TextureManager.getItemImage(item.getType());
            
            if (itemImage != null && !itemImage.isError()) {
                ImageView itemView = new ImageView(itemImage);
                itemView.setFitWidth(TILE_SIZE * SCALE * 0.6); // 道具大小为格子的60%
                itemView.setFitHeight(TILE_SIZE * SCALE * 0.6);
                itemView.setPreserveRatio(true);
                
                // 设置位置（道具在格子中心）
                itemView.setX(item.getPosition().getX() - TILE_SIZE * SCALE * 0.3);
                itemView.setY(item.getPosition().getY() - TILE_SIZE * SCALE * 0.3);
                
                // 添加闪烁效果
                itemView.setOpacity(0.8);
                
                System.out.println("成功加载道具图像: " + item.getType());
                return itemView;
            }
        } catch (Exception e) {
            System.out.println("无法加载道具图像: " + item.getType() + ", 错误: " + e.getMessage() + ", 使用颜色方块代替");
        }
        
        // 回退到颜色方块
        Rectangle itemRect = new Rectangle(TILE_SIZE * SCALE * 0.6, TILE_SIZE * SCALE * 0.6);
        itemRect.setX(item.getPosition().getX() - TILE_SIZE * SCALE * 0.3);
        itemRect.setY(item.getPosition().getY() - TILE_SIZE * SCALE * 0.3);
        
        // 根据道具类型设置颜色
        switch (item.getType()) {
            case BOOTS:
                itemRect.setFill(Color.BLUE);
                break;
            case LIFE_ELIXIR:
                itemRect.setFill(Color.RED);
                break;
            default:
                itemRect.setFill(Color.YELLOW);
        }
        
        itemRect.setStroke(Color.WHITE);
        itemRect.setStrokeWidth(2);
        itemRect.setOpacity(0.8);
        
        return itemRect;
    }
    
    private void createExplosionVisual(List<Point2D> explosionRange) {
        System.out.println("创建爆炸特效，影响位置数量: " + explosionRange.size());
        // 清除之前的爆炸效果
        root.getChildren().removeAll(explosionVisuals);
        explosionVisuals.clear();
        
        // 创建新的爆炸效果 - 以炸弹位置为中心的十字
        for (int i = 0; i < explosionRange.size(); i++) {
            Point2D pos = explosionRange.get(i);
            
            // 创建爆炸中心效果（更明显的圆形）
            Circle explosionCenter = new Circle();
            explosionCenter.setCenterX(pos.getX() + TILE_SIZE * SCALE / 2.0);
            explosionCenter.setCenterY(pos.getY() + TILE_SIZE * SCALE / 2.0);
            explosionCenter.setRadius(TILE_SIZE * SCALE / 2.0);
            explosionCenter.setFill(Color.YELLOW);
            explosionCenter.setStroke(Color.RED);
            explosionCenter.setStrokeWidth(3);
            explosionCenter.setOpacity(0.9);
            
            // 创建爆炸边框效果
            Rectangle explosionBorder = new Rectangle(TILE_SIZE * SCALE, TILE_SIZE * SCALE);
            explosionBorder.setX(pos.getX());
            explosionBorder.setY(pos.getY());
            explosionBorder.setFill(Color.TRANSPARENT);
            explosionBorder.setStroke(Color.RED);
            explosionBorder.setStrokeWidth(4);
            explosionBorder.setOpacity(1.0);
            
            // 调试信息：打印每个爆炸位置
            System.out.println("爆炸位置 " + i + ": (" + pos.getX() + ", " + pos.getY() + ")");
            
            explosionVisuals.add(explosionCenter);
            explosionVisuals.add(explosionBorder);
            root.getChildren().add(explosionCenter);
            root.getChildren().add(explosionBorder);
            
            // 添加缩放动画效果
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), explosionCenter);
            scaleTransition.setFromX(0.1);
            scaleTransition.setFromY(0.1);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            // 添加淡入淡出动画效果
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), explosionCenter);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(0.9);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setCycleCount(2);
            fadeTransition.play();
            
            // 为边框也添加淡入淡出效果
            FadeTransition borderFadeTransition = new FadeTransition(Duration.millis(2000), explosionBorder);
            borderFadeTransition.setFromValue(0.0);
            borderFadeTransition.setToValue(1.0);
            borderFadeTransition.setAutoReverse(true);
            borderFadeTransition.setCycleCount(2);
            borderFadeTransition.play();
        }
        
        // 2秒后清除爆炸效果，给玩家更多时间看到特效
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(2000));
        pause.setOnFinished(event -> {
            root.getChildren().removeAll(explosionVisuals);
            explosionVisuals.clear();
            System.out.println("爆炸特效已清除");
        });
        pause.play();
    }
    
    private void updateVisuals() {
        // 更新玩家位置和动画
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            ImageView playerVisual = playerVisuals.get(playerId);
            Point2D position = playerComponent.getPlayer().getPosition();
            
            // 检查玩家是否死亡，如果死亡则隐藏玩家视觉
            if (!playerComponent.getPlayer().isAlive()) {
                playerVisual.setVisible(false);
                // 清除该玩家的炸弹视觉
                List<javafx.scene.Node> playerBombVisuals = bombVisuals.get(playerId);
                if (playerBombVisuals != null) {
                    root.getChildren().removeAll(playerBombVisuals);
                    playerBombVisuals.clear();
                }
                continue; // 跳过死亡玩家的其他更新
            } else {
                playerVisual.setVisible(true);
            }
            
            // 更新位置
            playerVisual.setX(position.getX() - 8);
            playerVisual.setY(position.getY() - 16);
            
            // 检测移动方向并更新动画
            updatePlayerDirection(playerComponent);
        }
        
        // 更新炸弹视觉
        for (PlayerComponent playerComponent : players) {
            String playerId = playerComponent.getPlayer().getId();
            List<javafx.scene.Node> playerBombVisuals = bombVisuals.get(playerId);
            
            // 如果玩家死亡，跳过炸弹视觉更新
            if (!playerComponent.getPlayer().isAlive()) {
                continue;
            }
            
            // 清除旧的炸弹视觉
            root.getChildren().removeAll(playerBombVisuals);
            playerBombVisuals.clear();
            
            // 创建新的炸弹视觉
            for (Bomb bomb : playerComponent.getBombs().getActiveBombs()) {
                // 使用炸弹图像
                Image bombImage = TextureManager.getBombImage();
                if (bombImage != null) {
                    ImageView bombVisual = new ImageView(bombImage);
                    bombVisual.setFitWidth(TILE_SIZE * SCALE * 0.8); // 增大到格子的80%
                    bombVisual.setFitHeight(TILE_SIZE * SCALE * 0.8);
                    bombVisual.setPreserveRatio(true);
                    
                    // 使用世界坐标直接渲染炸弹，居中显示
                    bombVisual.setX(bomb.getWorldX() - TILE_SIZE * SCALE * 0.4);
                    bombVisual.setY(bomb.getWorldY() - TILE_SIZE * SCALE * 0.4);
                    
                    // 根据炸弹状态设置透明度
                    switch (bomb.getState()) {
                        case PLACED:
                            bombVisual.setOpacity(1.0);
                            break;
                        case TICKING:
                            // 根据引信进度改变透明度
                            double fuseProgress = bomb.getFuseProgress();
                            if (fuseProgress < 0.3) {
                                bombVisual.setOpacity(1.0);
                            } else if (fuseProgress < 0.6) {
                                bombVisual.setOpacity(0.8);
                            } else {
                                bombVisual.setOpacity(0.6);
                            }
                            break;
                        case EXPLODING:
                            bombVisual.setOpacity(0.3);
                            break;
                        default:
                            bombVisual.setOpacity(1.0);
                    }
                    
                    playerBombVisuals.add(bombVisual);
                    root.getChildren().add(bombVisual);
                } else {
                    // 回退到颜色方块
                    Rectangle bombVisual = new Rectangle(TILE_SIZE * SCALE * 0.8, TILE_SIZE * SCALE * 0.8);
                    bombVisual.setX(bomb.getWorldX() - TILE_SIZE * SCALE * 0.4);
                    bombVisual.setY(bomb.getWorldY() - TILE_SIZE * SCALE * 0.4);
                    bombVisual.setFill(Color.BLACK);
                    bombVisual.setStroke(Color.WHITE);
                    bombVisual.setStrokeWidth(2);
                    
                    playerBombVisuals.add(bombVisual);
                    root.getChildren().add(bombVisual);
                }
            }
        }
    }
    
    private void updateUI() {
        // 使用UIManager更新UI
        if (uiManager != null) {
            uiManager.updatePlayerInfo(players);
            uiManager.updateStatusBar(gameRunning, currentMapName);
        }
    }
    
    private void resetGame() {
        // 重置所有玩家
        for (PlayerComponent playerComponent : players) {
            com.stardewbombers.shared.entity.Player player = playerComponent.getPlayer();
            Point2D resetPosition;
            
            if (player.getId().equals("player1")) {
                resetPosition = new Point2D(3 * TILE_SIZE + TILE_SIZE/2, 3 * TILE_SIZE + TILE_SIZE/2);
            } else if (player.getId().equals("player2")) {
                resetPosition = new Point2D(11 * TILE_SIZE + TILE_SIZE/2, 9 * TILE_SIZE + TILE_SIZE/2);
            } else if (player.getId().equals("player3")) {
                // 玩家3重新随机生成位置
                resetPosition = findValidSpawnPosition();
            } else {
                resetPosition = new Point2D(7 * TILE_SIZE + TILE_SIZE/2, 7 * TILE_SIZE + TILE_SIZE/2);
            }
            
            playerComponent.getPlayer().setPosition(resetPosition);
            // 重置生命值（这里需要重新创建玩家，因为生命值是final的）
        }
        
        // 清除所有炸弹
        for (PlayerComponent playerComponent : players) {
            playerComponent.getBombs().clearAllBombs();
        }
        
        // 清除爆炸效果
        root.getChildren().removeAll(explosionVisuals);
        explosionVisuals.clear();
        
        // 清除道具视觉
        root.getChildren().removeAll(itemVisuals);
        itemVisuals.clear();
        
        // 重新初始化游戏
        initializeGame();
    }
    
    private void updatePlayerDirection(PlayerComponent playerComponent) {
        // 检测玩家移动方向并更新动画方向
        String playerId = playerComponent.getPlayer().getId();
        ImageView playerVisual = playerVisuals.get(playerId);
        PlayerAnimationManager animationManager = playerAnimationManagers.get(playerId);
        
        if (animationManager != null && playerVisual != null) {
            // 更新动画帧
            animationManager.updateFrame();
            
            // 获取当前帧的viewport设置
            int currentFrame = animationManager.getCurrentFrame();
            int sourceX = currentFrame * 16;
            int sourceY = animationManager.getCurrentDirection().getRow() * 32;
            
            // 更新玩家视觉的viewport
            playerVisual.setViewport(new javafx.geometry.Rectangle2D(
                sourceX, sourceY, 16, 32
            ));
            
            // 确保使用正确的角色图像
            PlayerAnimationManager.CharacterType assignedCharacter = playerCharacters.get(playerId);
            if (assignedCharacter != null && animationManager.getCurrentCharacter() != assignedCharacter) {
                animationManager.setCharacter(assignedCharacter);
                // 更新ImageView的图像
                playerVisual.setImage(animationManager.getCurrentFrameView().getImage());
            }
        }
    }
    
    
    /**
     * 处理游戏结束
     */
    private void handleGameEnd(String winnerId) {
        System.out.println("游戏结束！获胜者: " + (winnerId != null ? winnerId : "平局"));
        
        // 停止游戏循环
        gameRunning = false;
        
        // 在JavaFX应用线程中显示转盘抽奖界面
        javafx.application.Platform.runLater(() -> {
            try {
                // 使用单机版农场服务，确保与农场界面使用相同的数据源
                com.stardewbombers.farm.SinglePlayerFarmService farmService = com.stardewbombers.farm.FarmView.getGlobalFarmService();
                
                // 显示转盘抽奖界面，传递用户信息
                GameEndLotteryView lotteryView = new GameEndLotteryView(
                    (Stage) root.getScene().getWindow(), 
                    winnerId != null ? winnerId : "player1", // 如果没有获胜者，默认给player1
                    farmService,
                    currentUser // 传递当前登录用户信息
                );
                lotteryView.show();
            } catch (Exception e) {
                System.err.println("显示转盘抽奖界面时出错: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("启动PlayerBombVisualTest...");
            // 防止JavaFX应用程序自动退出
            javafx.application.Platform.setImplicitExit(false);
            launch(args);
        } catch (Exception e) {
            System.err.println("程序启动异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("程序主方法结束");
        }
    }
}
