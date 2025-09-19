@echo off
echo 测试lb.jpg背景图片登录界面...
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
echo 1. 查看lb.jpg图片是否作为背景显示
echo 2. 查看背景是否完全填充整个窗口
echo 3. 测试界面内容是否在背景之上显示
echo 4. 测试标签页切换功能
echo 5. 测试注册和登录功能
echo 6. 验证登录成功后跳转到主界面
echo.
echo 注意：控制台会显示背景加载的调试信息
echo.

call mvn javafx:run

pause
