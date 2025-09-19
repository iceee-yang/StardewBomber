# 暂停系统更新说明

## 功能概述

修改了游戏的暂停系统，将原来的P键暂停功能改为点击屏幕右上角ESC按钮同时暂停游戏和打开音量调节窗口。

## 修改内容

### 1. 移除P键暂停功能

**文件**: `PlayerBombVisualTest.java`
- 移除了P键的暂停/继续功能
- 简化了键盘控制逻辑

**修改前**:
```java
} else if (key == KeyCode.P) {
    // 暂停/继续
    gameRunning = !gameRunning;
} else if (key == KeyCode.TAB) {
```

**修改后**:
```java
} else if (key == KeyCode.TAB) {
```

### 2. 增强ESC按钮功能

**文件**: `UIManager.java`

#### 添加回调接口
```java
// 游戏暂停回调接口
public interface GamePauseCallback {
    void toggleGamePause();
}

private GamePauseCallback pauseCallback;
```

#### 添加回调设置方法
```java
/**
 * 设置游戏暂停回调
 */
public void setGamePauseCallback(GamePauseCallback callback) {
    this.pauseCallback = callback;
}
```

#### 修改ESC按钮点击事件
**修改前**:
```java
escButton.setOnMouseClicked(event -> {
    // 点击时的脉冲效果
    ScaleTransition pulseTransition = new ScaleTransition(Duration.millis(100), escButton);
    pulseTransition.setToX(0.9);
    pulseTransition.setToY(0.9);
    pulseTransition.setAutoReverse(true);
    pulseTransition.setCycleCount(2);
    pulseTransition.play();
    
    toggleVolumePanel();
});
```

**修改后**:
```java
escButton.setOnMouseClicked(event -> {
    // 点击时的脉冲效果
    ScaleTransition pulseTransition = new ScaleTransition(Duration.millis(100), escButton);
    pulseTransition.setToX(0.9);
    pulseTransition.setToY(0.9);
    pulseTransition.setAutoReverse(true);
    pulseTransition.setCycleCount(2);
    pulseTransition.play();
    
    // 同时打开音量面板和暂停游戏
    toggleVolumePanel();
    if (pauseCallback != null) {
        pauseCallback.toggleGamePause();
    }
});
```

### 3. 设置回调连接

**文件**: `PlayerBombVisualTest.java`

在 `createUI()` 方法中添加回调设置：
```java
// 设置游戏暂停回调
uiManager.setGamePauseCallback(() -> {
    gameRunning = !gameRunning;
    System.out.println("游戏状态切换: " + (gameRunning ? "运行中" : "暂停"));
});
```

### 4. 更新测试脚本

**文件**: `test_characters_simple.bat` 和 `test_third_player.bat`

更新了控制说明，移除了P键暂停的说明，添加了新的ESC按钮功能说明：

```
特殊功能：
- 点击屏幕右上角ESC按钮：暂停游戏 + 打开音量调节窗口
- 按R键：重置游戏
- 按TAB键：切换地图
```

## 技术实现

### 回调模式
使用回调接口模式实现UI组件与游戏逻辑的解耦：
- `UIManager` 定义回调接口
- `PlayerBombVisualTest` 实现回调逻辑
- 通过回调实现UI事件与游戏状态的同步

### 功能整合
ESC按钮现在同时执行两个操作：
1. 打开/关闭音量调节窗口
2. 暂停/继续游戏

### 用户体验
- 统一的暂停入口：只需要点击一个按钮
- 视觉反馈：按钮点击时有脉冲动画效果
- 状态同步：游戏暂停状态与音量窗口状态保持一致

## 使用方法

1. 运行游戏后，点击屏幕右上角的蓝色ESC按钮
2. 游戏会同时暂停并打开音量调节窗口
3. 再次点击ESC按钮会关闭音量窗口并继续游戏
4. 在音量窗口中可以调节音乐和音效音量

## 控制说明

### 玩家控制
- **玩家1**: WASD + Q键（放置炸弹）
- **玩家2**: 方向键 + 回车键（放置炸弹）
- **玩家3**: I/K/J/L + 空格键（放置炸弹）

### 特殊功能
- **点击ESC按钮**: 暂停游戏 + 打开音量调节窗口
- **R键**: 重置游戏

## 修复说明

### 问题修复
修复了关闭音量调节窗口后游戏仍然暂停的问题。

**修复前的问题**：
- 点击ESC按钮：暂停游戏 + 打开音量窗口
- 关闭音量窗口：游戏仍然暂停

**修复后的行为**：
- 点击ESC按钮：暂停游戏 + 打开音量窗口
- 关闭音量窗口：自动恢复游戏运行状态

### 技术实现
1. 修改 `closeVolumePanel()` 方法，在关闭音量窗口时调用暂停回调
2. 修改 `toggleVolumePanel()` 方法，在显示音量窗口时暂停游戏
3. 简化ESC按钮点击事件，只负责切换音量窗口状态

## 注意事项

- ESC按钮的暂停功能与音量窗口的开关状态是同步的
- 游戏暂停时，所有玩家移动和炸弹放置都会被暂停
- 音量调节窗口关闭时，游戏会自动继续运行
- 控制台会显示游戏状态切换的日志信息
- 音量窗口和游戏暂停状态始终保持一致
