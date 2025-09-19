@echo off
echo 测试标题颜色更新（深红棕色）...
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
echo 请查看以下更新：
echo 1. 标题 "StardewBombers" 现在是深红棕色 (#8B0000)
echo 2. 标题更加醒目和突出
echo 3. 与像素艺术风格背景形成良好对比
echo 4. 保持了双重阴影效果
echo.
echo 颜色对比：
echo - 之前：深棕色 (#8B4513)
echo - 现在：深红棕色 (#8B0000)
echo.

call mvn javafx:run

pause
