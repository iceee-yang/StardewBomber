@echo off
echo 测试带像素艺术背景的登录界面...
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
echo 1. 查看新的像素艺术风格登录界面
echo 2. 测试背景图片加载（如果已添加pixel_frame_background.png）
echo 3. 测试备用渐变背景（如果未添加背景图片）
echo 4. 测试注册和登录功能
echo 5. 验证登录成功后跳转到主界面
echo.

call mvn javafx:run

pause
