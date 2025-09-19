@echo off
echo 测试自定义标签页登录界面（完全透明）...
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
echo 1. 查看完全透明的自定义标签页界面
echo 2. 测试背景图片是否完全透过显示
echo 3. 测试标签页切换功能
echo 4. 测试像素艺术风格的渐变背景
echo 5. 测试半透明输入框和按钮
echo 6. 测试注册和登录功能
echo 7. 验证登录成功后跳转到主界面
echo.

call mvn javafx:run

pause
