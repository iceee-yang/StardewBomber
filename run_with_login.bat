@echo off
echo 启动带登录功能的StardewBombers游戏...
echo.

REM 先下载依赖
echo 下载依赖...
mvn dependency:copy-dependencies -q

REM 设置JavaFX模块路径
set MODULE_PATH=target\dependency

REM 设置类路径
set CLASSPATH=target\classes;target\dependency\*

echo 启动应用程序...
java --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -cp "%CLASSPATH%" com.stardewbombers.StardewBombersApp

pause
