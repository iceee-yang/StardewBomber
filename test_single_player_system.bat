@echo off
echo 测试单机版农场系统...
echo.

echo 编译项目...
call mvnw.cmd compile -q
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.

echo 运行抽奖测试...
java -cp "target/classes;lib/*" com.stardewbombers.client.LotteryTest

echo.
echo 测试完成！现在抽奖和农场使用同一数据源，无需playerId！
pause
