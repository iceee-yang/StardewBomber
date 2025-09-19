@echo off
echo 启动碰撞检测测试程序...
echo.

java --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出
pause
