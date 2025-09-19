package com.stardewbombers.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;
import com.stardewbombers.shared.entity.GameMap;

/**
 * 炸弹组件类 - 管理玩家的炸弹系统
 * 释放模式：同一时间只能存在 1 个炸弹；必须等爆炸/结束后才能放新的
 * 包含：网格吸附、爆炸范围计算
 */
public class BombComponent {
	private final String ownerId;
	private int bombPower;
	private final List<Bomb> activeBombs = new ArrayList<>();
	private final List<Bomb> explodedBombs = new ArrayList<>(); // 存储爆炸的炸弹
	private final int gridSize = 40; // 网格大小，与TILE_SIZE保持一致
	private GameMap gameMap; // 地图引用，用于边界检查

	public BombComponent(String ownerId, int bombCount, int bombPower) {
		this.ownerId = ownerId;
		this.bombPower = bombPower;
	}

	// 基础属性访问
	public String getOwnerId() { return ownerId; }
	public int getBombPower() { return bombPower; }
	public void setBombPower(int power) { this.bombPower = power; }
	public List<Bomb> getActiveBombs() { return activeBombs; }
	
	/**
	 * 设置游戏地图引用
	 * @param gameMap 游戏地图
	 */
	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	/**
	 * 放置炸弹（对齐到格子中心）
	 */
	public boolean placeBomb(Point2D worldPosition, long nowMs) {
		// 仅允许同一时间存在 1 个炸弹
		if (!activeBombs.isEmpty()) return false;
		
		// 将玩家位置对齐到最近的格子中心
		int gridX = (int)(worldPosition.getX() / gridSize);
		int gridY = (int)(worldPosition.getY() / gridSize);
		
		// 计算格子中心的世界坐标
		double alignedWorldX = gridX * gridSize + gridSize / 2.0;
		double alignedWorldY = gridY * gridSize + gridSize / 2.0;
		
		Bomb bomb = new Bomb(gridX, gridY, alignedWorldX, alignedWorldY, ownerId);
		bomb.setExplosionRadius(bombPower);
		bomb.startTicking();
		activeBombs.add(bomb);
		System.out.println("放置炸弹: " + ownerId + " 网格位置: (" + gridX + ", " + gridY + ") 世界坐标: (" + alignedWorldX + ", " + alignedWorldY + ") 状态: " + bomb.getState());
		return true;
	}

	/**
	 * 更新所有炸弹状态
	 */
	public List<Bomb> tick(long nowMs) {
		List<Bomb> exploded = new ArrayList<>();
		Iterator<Bomb> it = activeBombs.iterator();
		
		// 调试信息：显示活跃炸弹数量和调用栈
		if (!activeBombs.isEmpty()) {
			System.out.println("BombComponent.tick() 被调用 - 拥有者: " + ownerId + " 活跃炸弹列表大小: " + activeBombs.size());
			// 打印调用栈的前几层
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			for (int i = 1; i < Math.min(4, stack.length); i++) {
				System.out.println("  调用栈 " + i + ": " + stack[i].getClassName() + "." + stack[i].getMethodName() + ":" + stack[i].getLineNumber());
			}
		}
		
		while (it.hasNext()) {
			Bomb bomb = it.next();
			System.out.println("处理炸弹: " + bomb.getOwnerId() + " 状态: " + bomb.getState() + " 引信时间: " + bomb.getFuseTime());
			System.out.println("炸弹状态检查: " + bomb.getState() + " == " + Bomb.BombState.EXPLODING + " ? " + (bomb.getState() == Bomb.BombState.EXPLODING));
			
			// 更新倒计时
			if (bomb.getState() == Bomb.BombState.TICKING) {
				double deltaTime = 0.016; // 固定步长，后续可由调用方传入
				double newFuseTime = bomb.getFuseTime() - deltaTime;
				bomb.setFuseTime(newFuseTime);
				
				if (newFuseTime <= 0) {
					System.out.println("炸弹即将爆炸: " + bomb.getOwnerId() + " 位置: (" + bomb.getX() + ", " + bomb.getY() + ")");
					bomb.explode();
					// 炸弹开始爆炸，但不要立即移除，等爆炸动画完成
				}
			}
			
			// 检查爆炸状态
			if (bomb.getState() == Bomb.BombState.EXPLODING) {
				long currentTime = System.currentTimeMillis();
				
				// 如果炸弹刚开始爆炸且还没有被添加到爆炸列表，添加到爆炸列表
				boolean timeCheck = currentTime - bomb.getExplosionStartTime() < 100;
				boolean notAdded = !bomb.hasBeenAddedToExplodedList();
				System.out.println("炸弹 " + bomb.getOwnerId() + " 检查: 时间检查=" + timeCheck + " 未添加=" + notAdded + " 爆炸时间差=" + (currentTime - bomb.getExplosionStartTime()));
				
				if (notAdded && timeCheck) {
					System.out.println("炸弹开始爆炸，添加到爆炸列表: " + bomb.getOwnerId());
					exploded.add(bomb);
					explodedBombs.add(bomb); // 同时添加到explodedBombs列表
					bomb.setHasBeenAddedToExplodedList(true); // 标记为已添加，防止重复
					System.out.println("炸弹 " + bomb.getOwnerId() + " 已标记为已添加");
				} else if (!notAdded) {
					System.out.println("炸弹 " + bomb.getOwnerId() + " 已经被添加到爆炸列表，跳过");
				} else if (!timeCheck) {
					System.out.println("炸弹 " + bomb.getOwnerId() + " 爆炸时间已超过100ms，跳过");
				}
				
				// 检查爆炸动画是否结束
				if (currentTime - bomb.getExplosionStartTime() >= bomb.getExplosionDuration() * 1000) {
					bomb.setState(Bomb.BombState.EXPLODED);
					bomb.setHasExploded(true);
					it.remove();
				}
			}
		}
		
		return exploded;
	}

	/**
	 * 获取爆炸影响范围（十字形，以炸弹所在格子为中心，固定5个格子）
	 */
	public List<Point2D> getExplosionRange(Bomb bomb) {
		List<Point2D> affectedPositions = new ArrayList<>();
		
		// 将炸弹坐标对齐到格子中心
		int col = (int)(bomb.getWorldX() / gridSize);
		int row = (int)(bomb.getWorldY() / gridSize);
		
		// 直接使用格子坐标计算左上角位置
		double centerX = col * gridSize;
		double centerY = row * gridSize;

		// 中心点（炸弹所在格子）
		affectedPositions.add(new Point2D(centerX, centerY));

		// 上下左右各1格（固定十字形，总共5个格子），但需要检查边界
		// 上
		if (row > 0) {
			affectedPositions.add(new Point2D(centerX, centerY - gridSize));
		}
		// 下
		if (gameMap != null && row < gameMap.getHeight() - 1) {
			affectedPositions.add(new Point2D(centerX, centerY + gridSize));
		}
		// 左
		if (col > 0) {
			affectedPositions.add(new Point2D(centerX - gridSize, centerY));
		}
		// 右
		if (gameMap != null && col < gameMap.getWidth() - 1) {
			affectedPositions.add(new Point2D(centerX + gridSize, centerY));
		}

		return affectedPositions;
	}

	/**
	 * 检查位置是否在爆炸范围内（固定十字形，5个格子）
	 */
	public boolean isInExplosionRange(Point2D targetPosition, Bomb bomb) {
		// 将目标位置转换为网格坐标
		int targetGridX = (int)(targetPosition.getX() / gridSize);
		int targetGridY = (int)(targetPosition.getY() / gridSize);
		
		// 将炸弹位置转换为网格坐标
		int bombGridX = (int)(bomb.getWorldX() / gridSize);
		int bombGridY = (int)(bomb.getWorldY() / gridSize);
		
		// 检查是否在固定十字形爆炸范围内（中心+上下左右各1格）
		boolean inHorizontalRange = (targetGridX == bombGridX) && 
			Math.abs(targetGridY - bombGridY) <= 1;
		boolean inVerticalRange = (targetGridY == bombGridY) && 
			Math.abs(targetGridX - bombGridX) <= 1;
		
		return inHorizontalRange || inVerticalRange;
	}

	/**
	 * 强制爆炸所有炸弹
	 */
	public void forceExplodeAll() {
		for (Bomb bomb : activeBombs) {
			if (bomb.getState() == Bomb.BombState.TICKING) {
				bomb.explode();
			}
		}
	}

	/**
	 * 清除所有炸弹
	 */
	public void clearAllBombs() {
		activeBombs.clear();
		explodedBombs.clear();
	}
	
	/**
	 * 获取爆炸的炸弹列表
	 */
	public List<Bomb> getExplodedBombs() {
		return new ArrayList<>(explodedBombs);
	}
	
	/**
	 * 清理爆炸的炸弹列表
	 */
	public void clearExplodedBombs() {
		explodedBombs.clear();
	}
}

