@echo off
echo 启动玩家和炸弹系统可视化测试程序...
echo.

REM 使用模块系统运行JavaFX程序
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出
pause
