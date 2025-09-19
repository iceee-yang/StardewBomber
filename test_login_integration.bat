@echo off
echo 测试登录注册功能与主界面的集成...
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

echo 启动应用程序...
echo 请测试以下功能：
echo 1. 注册新用户
echo 2. 登录现有用户
echo 3. 登录成功后应该跳转到主界面
echo.

call mvn javafx:run

pause
