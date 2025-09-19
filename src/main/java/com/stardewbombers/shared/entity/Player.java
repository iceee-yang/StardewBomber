package com.stardewbombers.shared.entity;

import javafx.geometry.Point2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import com.stardewbombers.shared.enums.PowerUpType;
import com.stardewbombers.shared.util.GameConfig;
import com.stardewbombers.shared.entity.Item;
import com.stardewbombers.client.MusicManager;

public class Player {
	public enum Status { NORMAL, INVINCIBLE, DEAD }

	private final String id;
	private int health;
	private Point2D position;
	private double speed;
	private Status status;
	private long invincibleUntilMs;
	private final Map<PowerUpType, Integer> powerUps;
	private int bombCount;
	private int bombPower;
	private long bootsEffectEndTime; // boots效果结束时间
	private double originalSpeed; // 原始速度

	public Player(String id, Point2D spawnPosition) {
		this.id = id;
		this.health = GameConfig.PLAYER_MAX_HP;
		this.position = spawnPosition;
		this.speed = GameConfig.PLAYER_BASE_SPEED;
		this.status = Status.NORMAL;
		this.invincibleUntilMs = 0L;
		this.powerUps = new EnumMap<PowerUpType, Integer>(PowerUpType.class);
		this.bombCount = GameConfig.INITIAL_BOMB_COUNT;
		this.bombPower = GameConfig.INITIAL_BOMB_POWER;
		this.bootsEffectEndTime = 0L;
		this.originalSpeed = GameConfig.PLAYER_BASE_SPEED;
	}

	public String getId() { return id; }
	public int getHealth() { return health; }
	public Point2D getPosition() { return position; }
	public double getSpeed() { return speed; }
	public Status getStatus() { return status; }
	public int getBombCount() { return bombCount; }
	public int getBombPower() { return bombPower; }

	public void setPosition(Point2D newPos) { this.position = newPos; }
	public void setSpeed(double speed) { this.speed = speed; }

	public boolean isAlive() { return status != Status.DEAD; }

	public void tick(long nowMs) {
		if (status == Status.INVINCIBLE && nowMs >= invincibleUntilMs) {
			status = Status.NORMAL;
			invincibleUntilMs = 0L;
		}
		
		// 检查boots效果是否结束
		if (bootsEffectEndTime > 0 && nowMs >= bootsEffectEndTime) {
			speed = originalSpeed;
			bootsEffectEndTime = 0L;
			System.out.println("玩家 " + id + " 的boots加速效果结束");
		}
	}

	public boolean takeDamage(int amount, long nowMs) {
		if (!isAlive()) return false;
		if (status == Status.INVINCIBLE) return false;
		this.health -= Math.max(0, amount);
		System.out.println("玩家 " + id + " 受到 " + amount + " 点伤害，剩余生命值: " + this.health);
		// 播放受伤音效
		MusicManager.playDamageSound();

		if (this.health <= 0) {
			this.health = 0;
			this.status = Status.DEAD;
			System.out.println("玩家 " + id + " 已死亡！");
			return true;
		}
		this.status = Status.INVINCIBLE;
		this.invincibleUntilMs = nowMs + GameConfig.INVINCIBLE_MS_AFTER_HIT;
		return true;
	}

	public void addPowerUp(PowerUpType type) {
		powerUps.merge(type, 1, Integer::sum);
		long currentTime = System.currentTimeMillis();
		
		switch (type) {
			case SPEED_BOOST:
				this.speed = this.speed + 0.5;
				// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
			
				break;
			case SHIELD:
				this.status = Status.INVINCIBLE;
				this.invincibleUntilMs = currentTime + GameConfig.INVINCIBLE_MS_AFTER_HIT;
				// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
				
				break;
			case BOMB_COUNT:
				this.bombCount++;
				// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
				
				break;
			case BOMB_POWER:
				this.bombPower++;
				// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
				
				break;
			case BOOTS:
				// boots效果：增加速度，持续10秒
				this.speed = this.originalSpeed * 1.5; // 增加50%速度
				this.bootsEffectEndTime = currentTime + 10000; // 10秒
				System.out.println("玩家 " + id + " 获得boots加速效果，持续10秒");
				// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
				
				break;
			case LIFE_ELIXIR:
				// 生命药水效果：恢复1点生命
				if (this.health < GameConfig.PLAYER_MAX_HP) {
					this.health = Math.min(this.health + 1, GameConfig.PLAYER_MAX_HP);
					System.out.println("玩家 " + id + " 使用生命药水，恢复1点生命，当前生命值: " + this.health);

					// 播放加速音效（和捡靴子一样的音效）
				MusicManager.playSpeedupSound();
				
				} else {
					System.out.println("玩家 " + id + " 生命值已满，无法使用生命药水");
				}
				break;
			default:
				break;
		}
	}
}
