package com.stardewbombers.client;

import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.component.PlayerComponent;
import com.stardewbombers.component.MovementComponent;
import com.stardewbombers.component.BombComponent;
import com.stardewbombers.shared.enums.PowerUpType;
import com.stardewbombers.shared.util.GameConfig;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * 玩家和炸弹系统测试程序
 * 测试玩家移动、炸弹放置、爆炸等功能
 */
public class PlayerBombSystemTest {
    
    public static void main(String[] args) {
        System.out.println("=== StardewBombers 玩家和炸弹系统测试 ===");
        
        // 测试玩家系统
        testPlayerSystem();
        
        System.out.println("\n" + "=".repeat(50));
        
        // 测试炸弹系统
        testBombSystem();
        
        System.out.println("\n" + "=".repeat(50));
        
        // 测试玩家和炸弹交互
        testPlayerBombInteraction();
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    /**
     * 测试玩家系统
     */
    private static void testPlayerSystem() {
        System.out.println("\n🔍 测试玩家系统:");
        
        // 创建玩家
        Player player = new Player("player1", new Point2D(100, 100));
        MovementComponent movement = new MovementComponent(new Point2D(100, 100), 2.0);
        BombComponent bombs = new BombComponent("player1", 1, 1);
        PlayerComponent playerComponent = new PlayerComponent(player, movement, bombs);
        
        // 测试初始状态
        System.out.println("初始状态:");
        System.out.println("  玩家ID: " + player.getId());
        System.out.println("  生命值: " + player.getHealth());
        System.out.println("  位置: " + player.getPosition());
        System.out.println("  速度: " + player.getSpeed());
        System.out.println("  状态: " + player.getStatus());
        System.out.println("  炸弹数量: " + player.getBombCount());
        System.out.println("  炸弹威力: " + player.getBombPower());
        System.out.println("  是否存活: " + player.isAlive());
        
        // 测试移动
        System.out.println("\n测试移动:");
        playerComponent.moveRight();
        System.out.println("  向右移动后位置: " + player.getPosition());
        
        playerComponent.moveDown();
        System.out.println("  向下移动后位置: " + player.getPosition());
        
        playerComponent.moveLeft();
        System.out.println("  向左移动后位置: " + player.getPosition());
        
        playerComponent.moveUp();
        System.out.println("  向上移动后位置: " + player.getPosition());
        
        // 测试道具效果
        System.out.println("\n测试道具效果:");
        player.addPowerUp(PowerUpType.SPEED_BOOST);
        System.out.println("  获得速度提升后速度: " + player.getSpeed());
        
        player.addPowerUp(PowerUpType.BOMB_COUNT);
        System.out.println("  获得炸弹数量提升后炸弹数: " + player.getBombCount());
        
        player.addPowerUp(PowerUpType.BOMB_POWER);
        System.out.println("  获得炸弹威力提升后威力: " + player.getBombPower());
        
        // 测试伤害系统
        System.out.println("\n测试伤害系统:");
        boolean tookDamage = player.takeDamage(1, System.currentTimeMillis());
        System.out.println("  受到1点伤害: " + tookDamage);
        System.out.println("  当前生命值: " + player.getHealth());
        System.out.println("  当前状态: " + player.getStatus());
        
        // 测试无敌时间
        System.out.println("  在无敌时间内再次受到伤害:");
        boolean tookDamageAgain = player.takeDamage(1, System.currentTimeMillis() + 1000);
        System.out.println("  是否受到伤害: " + tookDamageAgain);
        
        // 测试死亡
        System.out.println("\n测试死亡:");
        player.takeDamage(2, System.currentTimeMillis() + 3000);
        System.out.println("  受到2点伤害后生命值: " + player.getHealth());
        System.out.println("  是否存活: " + player.isAlive());
        System.out.println("  状态: " + player.getStatus());
    }
    
    /**
     * 测试炸弹系统
     */
    private static void testBombSystem() {
        System.out.println("\n💣 测试炸弹系统:");
        
        // 创建炸弹
        Bomb bomb = new Bomb(5, 5, "player1");
        System.out.println("创建炸弹:");
        System.out.println("  位置: (" + bomb.getX() + ", " + bomb.getY() + ")");
        System.out.println("  拥有者: " + bomb.getOwnerId());
        System.out.println("  初始状态: " + bomb.getState());
        System.out.println("  引信时间: " + bomb.getFuseTime());
        System.out.println("  爆炸范围: " + bomb.getExplosionRadius());
        
        // 测试炸弹状态变化
        System.out.println("\n测试炸弹状态变化:");
        bomb.startTicking();
        System.out.println("  开始倒计时后状态: " + bomb.getState());
        System.out.println("  引信进度: " + String.format("%.2f", bomb.getFuseProgress()));
        
        // 模拟时间流逝
        bomb.setFuseTime(1.5);
        System.out.println("  倒计时1.5秒后引信进度: " + String.format("%.2f", bomb.getFuseProgress()));
        
        bomb.setFuseTime(0.0);
        System.out.println("  引信时间归零后是否可以爆炸: " + bomb.canExplode());
        
        // 测试爆炸
        System.out.println("\n测试爆炸:");
        bomb.explode();
        System.out.println("  爆炸后状态: " + bomb.getState());
        System.out.println("  是否正在爆炸: " + bomb.isExploding());
        System.out.println("  爆炸进度: " + String.format("%.2f", bomb.getExplosionProgress()));
        
        // 测试炸弹组件
        System.out.println("\n测试炸弹组件:");
        BombComponent bombComponent = new BombComponent("player1", 1, 2);
        System.out.println("  炸弹威力: " + bombComponent.getBombPower());
        
        // 测试放置炸弹
        boolean placed = bombComponent.placeBomb(new Point2D(250, 250), System.currentTimeMillis());
        System.out.println("  放置炸弹成功: " + placed);
        System.out.println("  活跃炸弹数量: " + bombComponent.getActiveBombs().size());
        
        // 测试爆炸范围
        List<Point2D> explosionRange = bombComponent.getExplosionRange(bombComponent.getActiveBombs().get(0));
        System.out.println("  爆炸影响位置数量: " + explosionRange.size());
        System.out.println("  爆炸范围位置:");
        for (Point2D pos : explosionRange) {
            System.out.println("    (" + pos.getX() + ", " + pos.getY() + ")");
        }
    }
    
    /**
     * 测试玩家和炸弹交互
     */
    private static void testPlayerBombInteraction() {
        System.out.println("\n🎮 测试玩家和炸弹交互:");
        
        // 创建两个玩家
        Player player1 = new Player("player1", new Point2D(100, 100));
        Player player2 = new Player("player2", new Point2D(200, 100));
        
        MovementComponent movement1 = new MovementComponent(new Point2D(100, 100), 2.0);
        MovementComponent movement2 = new MovementComponent(new Point2D(200, 100), 2.0);
        
        BombComponent bombs1 = new BombComponent("player1", 1, 2);
        BombComponent bombs2 = new BombComponent("player2", 1, 1);
        
        PlayerComponent playerComponent1 = new PlayerComponent(player1, movement1, bombs1);
        PlayerComponent playerComponent2 = new PlayerComponent(player2, movement2, bombs2);
        
        System.out.println("创建两个玩家:");
        System.out.println("  玩家1位置: " + player1.getPosition());
        System.out.println("  玩家2位置: " + player2.getPosition());
        
        // 玩家1放置炸弹
        System.out.println("\n玩家1放置炸弹:");
        boolean placed = playerComponent1.placeBomb(System.currentTimeMillis());
        System.out.println("  放置成功: " + placed);
        
        // 模拟炸弹爆炸
        System.out.println("\n模拟炸弹爆炸:");
        Bomb bomb = bombs1.getActiveBombs().get(0);
        bomb.explode();
        
        // 创建爆炸事件
        List<Point2D> explosionRange = bombs1.getExplosionRange(bomb);
        ExplosionEvent explosionEvent = new ExplosionEvent(bomb, explosionRange, System.currentTimeMillis());
        
        System.out.println("  爆炸事件创建成功");
        System.out.println("  爆炸拥有者: " + explosionEvent.getOwnerId());
        System.out.println("  影响位置数量: " + explosionEvent.getAffectedPositions().size());
        
        // 测试玩家是否在爆炸范围内
        System.out.println("\n测试爆炸伤害:");
        boolean player1InRange = playerComponent1.isInExplosionRange(explosionEvent);
        boolean player2InRange = playerComponent2.isInExplosionRange(explosionEvent);
        
        System.out.println("  玩家1在爆炸范围内: " + player1InRange);
        System.out.println("  玩家2在爆炸范围内: " + player2InRange);
        
        // 测试伤害处理
        if (player2InRange) {
            boolean tookDamage = playerComponent2.handleExplosionDamage(explosionEvent);
            System.out.println("  玩家2受到伤害: " + tookDamage);
            System.out.println("  玩家2当前生命值: " + player2.getHealth());
        }
        
        // 测试时间更新
        System.out.println("\n测试时间更新:");
        long currentTime = System.currentTimeMillis();
        playerComponent1.tick(currentTime);
        playerComponent2.tick(currentTime);
        System.out.println("  时间更新完成");
        
        // 测试炸弹状态更新
        List<Bomb> explodedBombs = bombs1.tick(currentTime);
        System.out.println("  爆炸的炸弹数量: " + explodedBombs.size());
    }
}
