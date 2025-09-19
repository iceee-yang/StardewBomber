@echo off
echo 启动碰撞检测测试程序（不使用模块系统）...
echo.

java -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出
pause
