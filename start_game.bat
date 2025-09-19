@echo off
echo 正在启动 StardewBombers 碰撞检测测试程序...
echo.

REM 尝试不同的启动方式
echo 方式1: 使用 --add-modules 参数
java --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

if %errorlevel% neq 0 (
    echo.
    echo 方式1失败，尝试方式2: 使用 --module-path
    java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest
)

if %errorlevel% neq 0 (
    echo.
    echo 方式2失败，尝试方式3: 不使用模块系统
    java -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest
)

echo.
echo 程序已退出，错误代码: %errorlevel%
pause
