@echo off
echo 启动调试版本的PlayerBombVisualTest...
echo.

REM 设置Java选项
set JAVA_OPTS=--module-path "lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -Djavafx.verbose=true -Djava.awt.headless=false

REM 运行程序
java %JAVA_OPTS% -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 程序已退出，退出代码: %ERRORLEVEL%
pause
