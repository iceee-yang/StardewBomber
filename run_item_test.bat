@echo off
echo 运行道具系统测试...
cd /d "%~dp0"

echo 编译项目...
call mvnw.cmd compile -q

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 运行道具系统测试...
java -cp "target/classes;lib/*" com.stardewbombers.client.ItemSystemTest

echo 测试完成！
pause
