@echo off
echo 测试美化后的登录界面（像素艺术风格）...
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
echo 1. 查看像素艺术风格的背景图片
echo 2. 查看与背景协调的界面配色
echo 3. 查看深棕色主题的界面元素
echo 4. 查看米色输入框和按钮
echo 5. 测试标签页切换功能
echo 6. 测试注册和登录功能
echo 7. 验证登录成功后跳转到主界面
echo.
echo 界面特色：
echo - 深棕色 (#8B4513) 主题配色
echo - 米色 (#F5F5DC) 背景和文字
echo - 像素艺术风格边框和阴影
echo - 与背景图片完美融合
echo.

call mvn javafx:run

pause
