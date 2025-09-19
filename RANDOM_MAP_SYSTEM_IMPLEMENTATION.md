# 随机地图系统实现说明

## 功能概述

为游戏添加了随机地图选择系统，每次开始游戏时从3张可用地图中随机选择一张，并且地图切换也使用随机选择。

## 实现细节

### 1. 可用地图列表

**文件**: `PlayerBombVisualTest.java`

```java
// 可用地图列表
private static final String[] AVAILABLE_MAPS = {"cave_map", "home_map", "farm_map"};
```

### 2. 随机地图选择方法

```java
/**
 * 随机选择地图
 */
private void selectRandomMap() {
    int randomIndex = (int) (Math.random() * AVAILABLE_MAPS.length);
    currentMapName = AVAILABLE_MAPS[randomIndex];
    System.out.println("随机选择地图: " + currentMapName);
}
```

### 3. 游戏初始化时的地图选择

**修改前**:
```java
private String currentMapName = "cave_map"; // 固定使用洞穴地图
```

**修改后**:
```java
private String currentMapName; // 动态设置

private void initializeGame() {
    // 初始化音乐管理器
    MusicManager.initialize();
    
    // 随机选择地图
    selectRandomMap();
    
    // 加载Tiled地图
    try {
        gameMap = MapLoader.loadMap(currentMapName);
        System.out.println("成功加载Tiled地图: " + currentMapName + ".json");
    } catch (Exception e) {
        System.out.println("加载Tiled地图失败，使用默认地图: " + e.getMessage());
        gameMap = SimpleMapLoader.createFarmMap();
    }
    // ...
}
```

### 4. 地图切换逻辑优化

**修改前**:
```java
private void switchMap() {
    // 切换地图
    if (currentMapName.equals("cave_map")) {
        currentMapName = "farm_map";
    } else if (currentMapName.equals("farm_map")) {
        currentMapName = "home_map";
    } else {
        currentMapName = "cave_map";
    }
    System.out.println("切换到地图: " + currentMapName);
    // ...
}
```

**修改后**:
```java
private void switchMap() {
    // 随机选择新地图（确保与当前地图不同）
    String newMapName;
    do {
        newMapName = AVAILABLE_MAPS[(int) (Math.random() * AVAILABLE_MAPS.length)];
    } while (newMapName.equals(currentMapName) && AVAILABLE_MAPS.length > 1);
    
    currentMapName = newMapName;
    System.out.println("随机切换到地图: " + currentMapName);
    // ...
}
```

## 功能特点

### 1. 随机性
- 游戏开始时随机选择地图
- 地图切换时随机选择新地图
- 确保切换时选择不同的地图（如果有多张地图）

### 2. 可扩展性
- 通过修改 `AVAILABLE_MAPS` 数组可以轻松添加新地图
- 地图选择逻辑自动适应地图数量

### 3. 用户体验
- 每次游戏都有不同的地图体验
- 地图切换更加有趣和不可预测
- 控制台显示地图选择信息

## 使用方法

### 游戏开始
- 启动游戏时会自动随机选择一张地图
- 控制台会显示选择的地图名称

### 地图切换
- 按 **TAB键** 随机切换到不同的地图
- 系统会确保切换到与当前地图不同的地图

### 游戏重置
- 按 **R键** 重置游戏时会重新随机选择地图

## 控制说明

### 玩家控制
- **玩家1**: WASD + Q键（放置炸弹）
- **玩家2**: 方向键 + 回车键（放置炸弹）
- **玩家3**: I/K/J/L + 空格键（放置炸弹）

### 特殊功能
- **点击ESC按钮**: 暂停游戏 + 打开音量调节窗口
- **R键**: 重置游戏（重新随机选择地图和角色）

## 地图信息

### 可用地图
1. **cave_map** - 洞穴地图
2. **home_map** - 家园地图  
3. **farm_map** - 农场地图

### 地图文件位置
- `src/main/resources/maps/cave_map.json`
- `src/main/resources/maps/home_map.json`
- `src/main/resources/maps/farm_map.json`

## 输出示例

```
随机选择地图: farm_map
成功加载Tiled地图: farm_map.json
玩家1 分配角色: ALEX
玩家2 分配角色: HALEY
玩家3 分配角色: ABIGAIL
```

## 技术实现

### 随机算法
- 使用 `Math.random()` 生成随机索引
- 确保地图切换时选择不同的地图

### 错误处理
- 如果地图加载失败，会回退到默认地图
- 控制台会显示详细的错误信息

### 状态管理
- 地图状态与游戏状态同步
- 重置游戏时重新初始化所有随机选择

## 注意事项

- 确保所有地图文件存在于 `maps` 目录中
- 地图文件格式必须符合 `MapLoader` 的要求
- 如果只有一张地图，切换功能会正常工作但不会切换
- 控制台会显示所有地图选择和切换的日志信息
