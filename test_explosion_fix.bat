@echo off
echo 测试爆炸修复...
echo.

REM 使用更稳定的JavaFX启动参数
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -Djavafx.verbose=true -Djava.awt.headless=false -Dprism.verbose=true -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出，退出代码: %ERRORLEVEL%
pause
