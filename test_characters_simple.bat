@echo off
echo 测试角色系统（简化版）...
echo.
echo 控制说明：
echo 玩家1: WASD + Q键
echo 玩家2: 方向键 + 回车键  
echo 玩家3: I/K/J/L + 空格键
echo.
echo 特殊功能：
echo - 点击屏幕右上角ESC按钮：暂停游戏 + 打开音量调节窗口
echo - 按R键：重置游戏
echo.
echo 地图系统：
echo - 游戏开始时随机从3张地图中选择一张
echo - 可用地图：cave_map, home_map, farm_map
echo - 按R键重置游戏会重新随机选择地图
echo.
echo 角色系统：
echo - 游戏开始时随机从4个角色中选择3个不重复的角色
echo - 可用角色：Alex, Abigail, Haley, Lewis
echo - 每个玩家都有独特的角色外观和动画
echo - 按R键重置游戏会重新随机分配角色
echo.
echo 按任意键启动游戏...
pause

java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 游戏已退出
pause
