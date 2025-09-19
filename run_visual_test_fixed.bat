@echo off
echo 启动玩家和炸弹系统可视化测试程序...
echo.

REM 尝试不同的模块组合
echo 尝试启动程序（不包含javafx.media模块）...
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

if %ERRORLEVEL% neq 0 (
    echo.
    echo 第一次尝试失败，尝试包含所有模块...
    java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest
)

echo.
echo 程序已退出
pause
