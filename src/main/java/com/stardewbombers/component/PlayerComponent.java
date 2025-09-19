package com.stardewbombers.component;

import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Player;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.ExplosionEvent;
import com.stardewbombers.shared.entity.Item;
import com.stardewbombers.shared.enums.PowerUpType;
import java.util.List;

public class PlayerComponent {
	private final Player player;
	private final MovementComponent movement;
	private final BombComponent bombs;

	public PlayerComponent(Player player, MovementComponent movement, BombComponent bombs) {
		this.player = player;
		this.movement = movement;
		this.bombs = bombs;
	}

	public Player getPlayer() { return player; }
	public MovementComponent getMovement() { return movement; }
	public BombComponent getBombs() { return bombs; }

	public void moveUp() { if (player.isAlive()) movement.moveUp(); }
	public void moveDown() { if (player.isAlive()) movement.moveDown(); }
	public void moveLeft() { if (player.isAlive()) movement.moveLeft(); }
	public void moveRight() { if (player.isAlive()) movement.moveRight(); }

	public boolean placeBomb(long nowMs) {
		System.out.println("PlayerComponent.placeBomb() 调用 - 玩家存活状态: " + player.isAlive());
		if (!player.isAlive()) {
			System.out.println("玩家已死亡，无法放置炸弹");
			return false;
		}
		Point2D pos = player.getPosition();
		System.out.println("玩家位置: (" + pos.getX() + ", " + pos.getY() + ")");
		boolean result = bombs.placeBomb(pos, nowMs);
		System.out.println("BombComponent.placeBomb() 返回结果: " + result);
		return result;
	}

	/**
	 * 处理爆炸伤害
	 */
	public boolean handleExplosionDamage(ExplosionEvent explosionEvent) {
		System.out.println("处理爆炸伤害 - 玩家: " + player.getId() + " 炸弹: " + explosionEvent.getBomb().getOwnerId() + " 时间: " + explosionEvent.getExplosionTime());
		
		if (!player.isAlive()) {
			System.out.println("玩家 " + player.getId() + " 已死亡，跳过伤害处理");
			return false;
		}
		
		// 检查玩家是否在爆炸范围内
		Point2D playerPos = player.getPosition();
		boolean inRange = bombs.isInExplosionRange(playerPos, explosionEvent.getBomb());
		
		if (inRange) {
			System.out.println("玩家 " + player.getId() + " 在爆炸范围内，造成伤害");
			// 炸弹可以炸到包括自己在内的所有玩家
			return player.takeDamage(1, explosionEvent.getExplosionTime());
		} else {
			System.out.println("玩家 " + player.getId() + " 不在爆炸范围内");
		}
		return false;
	}

	/**
	 * 检查玩家是否在爆炸范围内
	 */
	public boolean isInExplosionRange(ExplosionEvent explosionEvent) {
		Point2D playerPos = player.getPosition();
		return bombs.isInExplosionRange(playerPos, explosionEvent.getBomb());
	}

	public void tick(long nowMs) {
		player.tick(nowMs);
		bombs.tick(nowMs);
		syncPosition(); // 每帧同步移动组件的位置到玩家实体
		
		// 处理道具拾取
		handleItemPickup();
	}

	private void syncPosition() {
		Point2D p = movement.getPosition();
		player.setPosition(p);
	}
	
	/**
	 * 处理道具拾取
	 */
	private void handleItemPickup() {
		List<Item> pickedUpItems = movement.getPickedUpItems();
		for (Item item : pickedUpItems) {
			// 将道具类型转换为PowerUpType并应用效果
			PowerUpType powerUpType = convertItemToPowerUpType(item.getType());
			player.addPowerUp(powerUpType);
		}
	}
	
	/**
	 * 将Item类型转换为PowerUpType
	 */
	private PowerUpType convertItemToPowerUpType(com.stardewbombers.shared.enums.PowerUpType itemType) {
		// 这里直接返回，因为Item使用的是PowerUpType枚举
		return itemType;
	}
}
