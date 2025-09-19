# 角色系统实现说明

## 功能概述

为游戏添加了角色系统，从4个可用角色中随机选择3个不重复的角色分配给3个玩家。

## 实现细节

### 1. 角色资源
- **位置**: `src/main/resources/character(16-32)/`
- **可用角色**: 
  - Alex.png
  - Abigail.png  
  - Haley.png
  - Lewis.png
- **图像格式**: 每行4个图像代表朝一个方向运动的帧，一共四行（上下左右）

### 2. 核心功能

#### 随机角色选择
- 游戏开始时从4个角色中随机选择3个不重复的角色
- 使用 `Collections.shuffle()` 确保随机性
- 每个玩家分配一个独特的角色

#### 角色分配逻辑
```java
private void selectRandomCharacters() {
    // 获取所有可用的角色类型
    PlayerAnimationManager.CharacterType[] allCharacters = PlayerAnimationManager.CharacterType.values();
    
    // 创建角色列表并打乱顺序
    List<CharacterType> characterList = new ArrayList<>();
    for (CharacterType character : allCharacters) {
        characterList.add(character);
    }
    Collections.shuffle(characterList);
    
    // 为3个玩家分配前3个角色
    String[] playerIds = {"player1", "player2", "player3"};
    for (int i = 0; i < 3; i++) {
        playerCharacters.put(playerIds[i], characterList.get(i));
    }
}
```

### 3. 修改的文件

#### PlayerBombVisualTest.java
- 添加了 `playerCharacters` 映射来存储玩家角色分配
- 实现了 `selectRandomCharacters()` 方法
- 修改了 `createPlayer()` 方法以使用分配的角色
- 更新了 `updatePlayerDirection()` 方法确保正确的角色显示
- 在重置游戏和切换地图时重新随机分配角色

#### PlayerAnimationManager.java
- 已经包含了角色类型枚举和图像加载功能
- 支持设置和切换角色

### 4. 游戏控制

#### 玩家控制
- **玩家1**: WASD + Q键（放置炸弹）
- **玩家2**: 方向键 + 回车键（放置炸弹）
- **玩家3**: I/K/J/L + 空格键（放置炸弹）

#### 特殊功能
- **R键**: 重置游戏，重新随机分配角色
- **P键**: 暂停/继续游戏

### 5. 测试脚本

#### test_characters_simple.bat
- 简化版测试脚本，不依赖javafx.media模块
- 包含完整的控制说明和角色系统说明

#### test_third_player.bat
- 完整版测试脚本，包含所有模块

## 技术特点

1. **随机性**: 每次游戏开始都会有不同的角色组合
2. **不重复**: 确保3个玩家使用不同的角色
3. **动态更新**: 重置游戏或切换地图时重新分配角色
4. **动画支持**: 每个角色都有完整的4方向动画
5. **资源管理**: 自动加载和管理角色图像资源

## 使用方法

1. 运行 `test_characters_simple.bat` 启动游戏
2. 观察控制台输出，查看角色分配信息
3. 在游戏中可以看到3个不同的角色
4. 使用R键或TAB键测试角色重新分配功能

## 输出示例

```
玩家1 分配角色: ALEX
玩家2 分配角色: HALEY  
玩家3 分配角色: ABIGAIL
为玩家 player1 设置角色: ALEX
为玩家 player2 设置角色: HALEY
为玩家 player3 设置角色: ABIGAIL
```

## 注意事项

- 确保 `character(16-32)` 文件夹中有所有4个角色图像文件
- 角色图像格式必须符合16x32像素，4x4帧的规格
- 如果缺少角色图像文件，会在控制台显示警告信息
