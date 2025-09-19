@echo off
echo 测试数据库连接...
echo.

echo 编译项目...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.

echo 下载依赖...
call mvn dependency:copy-dependencies -q

echo 运行数据库连接测试...
java -cp "target\classes;target\dependency\*" com.stardewbombers.db.Database

echo.
echo 数据库连接测试完成！
pause
