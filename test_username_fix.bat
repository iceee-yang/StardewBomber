@echo off
echo 测试用户名显示修复...
echo.

echo 编译项目...
call mvnw.cmd compile -q
if %errorlevel% neq 0 (
    echo 编译失败！请检查错误信息。
    pause
    exit /b 1
)

echo ✅ 编译成功！
echo.

echo 运行带登录的游戏测试...
echo 请按照以下步骤测试：
echo 1. 在登录界面输入手机号和密码
echo 2. 登录成功后进入主菜单
echo 3. 点击"开始游戏"进入游戏
echo 4. 让两个玩家死亡触发游戏结束
echo 5. 在抽奖界面点击"返回主界面"
echo 6. 检查右上角是否显示正确的用户名
echo.

java -cp "target/classes;lib/*" com.stardewbombers.StardewBombersApp

echo.
echo 测试完成！
pause
