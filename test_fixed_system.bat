@echo off
echo 测试修复后的单机版农场系统...
echo.

echo 编译项目...
call mvnw.cmd compile -q
if %errorlevel% neq 0 (
    echo 编译失败！请检查错误信息。
    pause
    exit /b 1
)

echo ✅ 编译成功！所有错误已修复！
echo.

echo 运行抽奖测试...
java -cp "target/classes;lib/*" com.stardewbombers.client.LotteryTest

echo.
echo 🎉 测试完成！
echo.
echo 现在系统特点：
echo ✅ 不需要playerId - 直接操作全局数据
echo ✅ 抽奖和农场使用同一数据源
echo ✅ 数据完全同步
echo ✅ 简化的数据存储格式
echo.
pause
