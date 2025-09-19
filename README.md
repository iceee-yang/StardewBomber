# 星露谷泡泡堂StardewBomber
Project for 2025 CSU-SE Java training
#### 项目介绍
这是一款基于“Q版泡泡堂”玩法、融合“星露谷物语”农场系统与美术元素的多人联机小游戏，使用基于JavaFX的FXGL框架开发。核心玩法主要为躲避危险，放置炸弹把其他玩家炸死，最终获得游戏胜利。附带了农场系统，玩家进行游戏对局可获得“种子”，种子在农场播种后可收获农作物，农作物可作为局内道具在游戏对局中带来增益效果。
#### 技术栈
本项目采用基于JavaFX的FXGL框架作为客户端应用开发框架，结合Maven项目结构管理，构建了丰富的图形用户界面与游戏渲染层。基于Tiled地图编辑器构建游戏世界，并解析其数据格式以实现高效的地图渲染与交互。数据持久层采用MySQL关系型数据库，负责管理玩家账户、游戏进度与全局状态。
#### 项目架构与模块定义
    ···
    StardewBombers/  
    ├── pom.xml                           # Maven配置文件  
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/stardewbombers/
    │   │   │       ├── StardewBombersApp.java  # 主启动类
    │   │   │       ├── client/           # 客户端模块
    │   │   │       │   ├── GameClient.java
    │   │   │       │   ├── ui/           # UI界面
    │   │   │       │   │   ├── MainMenuView.java
    │   │   │       │   │   ├── GameView.java
    │   │   │       │   │   ├── FarmView.java
    │   │   │       │   │   └── LobbyView.java
    │   │   │       │   ├── controller/   # 客户端控制器
    │   │   │       │   │   ├── GameController.java
    │   │   │       │   │   ├── PlayerController.java
    │   │   │       │   │   └── FarmController.java
    │   │   │       │   └── network/      # 网络通信
    │   │   │       │       ├── NetworkClient.java
    │   │   │       │       └── MessageHandler.java
    │   │   │       ├── server/           # 服务端模块
    │   │   │       │   ├── GameServer.java
    │   │   │       │   ├── network/
    │   │   │       │   │   ├── NetworkServer.java
    │   │   │       │   │   └── ClientHandler.java
    │   │   │       │   ├── game/
    │   │   │       │   │   ├── GameRoom.java
    │   │   │       │   │   ├── GameLogic.java
    │   │   │       │   │   └── CollisionDetector.java
    │   │   │       │   └── database/
    │   │   │       │       ├── DatabaseManager.java
    │   │   │       │       └── PlayerDataDAO.java
    │   │   │       ├── shared/           # 共享模块
    │   │   │       │   ├── entity/       # 游戏实体
    │   │   │       │   │   ├── Player.java
    │   │   │       │   │   ├── Bomb.java
    │   │   │       │   │   ├── PowerUp.java
    │   │   │       │   │   ├── Block.java
    │   │   │       │   │   └── farm/
    │   │   │       │   │       ├── Crop.java
    │   │   │       │   │       ├── Seed.java
    │   │   │       │   │       └── Farm.java
    │   │   │       │   ├── protocol/     # 网络协议
    │   │   │       │   │   ├── Message.java
    │   │   │       │   │   ├── GameMessage.java
    │   │   │       │   │   ├── FarmMessage.java
    │   │   │       │   │   └── MessageType.java
    │   │   │       │   ├── enums/        # 枚举类
    │   │   │       │   │   ├── GameState.java
    │   │   │       │   │   ├── PowerUpType.java
    │   │   │       │   │   ├── CropType.java
    │   │   │       │   │   └── BlockType.java
    │   │   │       │   └── util/         # 工具类
    │   │   │       │       ├── GameConfig.java
    │   │   │       │       ├── MapLoader.java
    │   │   │       │       └── JsonUtil.java
    │   │   │       └── component/        # FXGL组件
    │   │   │           ├── PlayerComponent.java
    │   │   │           ├── BombComponent.java
    │   │   │           ├── FarmComponent.java
    │   │   │           └── MovementComponent.java
    │   │   └── resources/
    │   │       ├── assets/               # 游戏资源
    │   │       │   ├── textures/         # 贴图资源
    │   │       │   │   ├── player/
    │   │       │   │   ├── bomb/
    │   │       │   │   ├── blocks/
    │   │       │   │   └── crops/
    │   │       │   ├── sounds/           # 音效资源
    │   │       │   └── maps/             # 地图文件
    │   │       │       └── default_map.json
    │   │       ├── fxml/                 # FXML布局文件
    │   │       │   ├── main_menu.fxml
    │   │       │   ├── game_view.fxml
    │   │       │   └── farm_view.fxml
    │   │       ├── css/                  # 样式文件
    │   │       │   └── game_style.css
    │   │       └── application.properties
    │   └── test/
    │       └── java/
    │           └── com/bombfarm/
    │               ├── GameLogicTest.java
    │               ├── FarmSystemTest.java
    │               └── NetworkTest.java
    ···

#### 接口设计
1. 游戏核心接口
  - 游戏状态管理接口IGameState
  - 玩家管理接口IPlayerManager
  - 炸弹系统接口IBombSystem
  - 碰撞检测接口ICollisionDetector
2. 农场系统接口
3. 网络通信接口
  - 网络客户端接口INetworkClient
  - 网络服务器端接口INetworkServer
  - 消息处理接口IMessageHandler
4. 数据持久化接口
5. UI界面接口
  - 游戏视图接口IGameView
  - 农场视图接口IFarmView

#### 参与贡献
- iceee-yang
- CallMeWangDaoZhang
- ningshuaiming
- Leecream
- fxw827510
