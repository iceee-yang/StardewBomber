 @echo off
echo 启动修复版本的PlayerBombVisualTest...
echo.

REM 使用更稳定的JavaFX启动参数
java --module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -Djavafx.verbose=true -Djava.awt.headless=false -Dprism.verbose=true -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出，退出代码: %ERRORLEVEL%
pause
