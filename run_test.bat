@echo off
echo 启动 StardewBombers 碰撞检测测试程序...
echo.

REM 设置 JavaFX 模块路径和模块
set MODULE_PATH=lib
set MODULES=javafx.controls,javafx.fxml,javafx.graphics,javafx.media

REM 运行程序
java --module-path "%MODULE_PATH%" --add-modules "%MODULES%" -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出
pause
