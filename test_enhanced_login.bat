@echo off
echo 测试增强版登录界面（背景优化）...
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
echo 1. 查看优化的背景显示效果
echo 2. 测试半透明内容面板
echo 3. 测试像素艺术风格的渐变背景
echo 4. 测试改进的输入框和按钮样式
echo 5. 测试注册和登录功能
echo 6. 验证登录成功后跳转到主界面
echo.

call mvn javafx:run

pause
