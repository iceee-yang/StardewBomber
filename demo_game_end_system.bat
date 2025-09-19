@echo off
echo ========================================
echo     StardewBombers 游戏结算系统演示
echo ========================================
echo.
echo 功能说明：
echo - 当两个玩家死亡时，游戏自动结束
echo - 弹出抽奖窗口
echo - 点击"开始抽奖"按钮进行抽奖
echo - 获得种子奖励并更新到农场库存
echo.
echo 控制说明：
echo - 玩家1: WASD移动，空格键放炸弹
echo - 玩家2: 方向键移动，回车键放炸弹
echo - 让两个玩家被炸弹炸死来触发游戏结束
echo.
echo 抽奖概率：
echo - 胡萝卜种子: 50%%
echo - 草莓种子: 30%%
echo - 土豆种子: 20%%
echo - 数量: 1个(70%%) 2个(25%%) 3个(5%%)
echo.
echo ========================================
echo.
echo 正在启动游戏...
echo.

java -cp "target/classes;lib/*" com.stardewbombers.client.PlayerBombVisualTest

echo.
echo 游戏已结束。
pause
