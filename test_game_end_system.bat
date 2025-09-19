@echo off
echo 测试游戏结算系统...
echo.

echo 编译项目...
call mvnw.cmd clean compile -q

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.
echo 启动游戏测试...
echo 注意：当两个玩家死亡时，应该会弹出转盘抽奖窗口
echo.

java -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

pause
