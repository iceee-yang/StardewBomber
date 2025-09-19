@echo off
echo 测试单机版农场系统数据同步...
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

echo 运行单机版农场测试...
java -cp "target/classes;lib/*" com.stardewbombers.client.LotteryTest

echo.
echo 测试完成！
pause
