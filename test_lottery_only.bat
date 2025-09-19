@echo off
echo 测试抽奖功能...
echo.

echo 编译项目...
call mvnw.cmd clean compile -q

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.
echo 启动抽奖测试程序...
echo 点击"测试抽奖"按钮多次，观察结果和库存变化
echo.

java -cp "target/classes;lib/*" com.stardewbombers.client.LotteryTest

pause
