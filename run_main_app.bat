@echo off
echo 正在启动 StardewBombers 主程序...
echo.

REM 编译项目（如果需要）
echo 检查编译状态...
if not exist "target\classes\com\stardewbombers\StardewBombersApp.class" (
    echo 编译主程序...
    javac -cp "target\classes;lib/*" -d target\classes src\main\java\com\stardewbombers\StardewBombersApp.java
    echo 编译完成！
    echo.
)

REM 运行主程序
echo 启动主程序...
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target\classes;lib/*" com.stardewbombers.StardewBombersApp

echo.
echo 程序已退出
pause
