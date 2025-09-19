package com.stardewbombers.component;

import javafx.geometry.Point2D;
import com.stardewbombers.server.game.CollisionDetector;
import com.stardewbombers.shared.entity.Item;
import com.stardewbombers.shared.entity.ItemManager;
import java.util.List;

public class MovementComponent {
	private Point2D position;
	private Point2D targetPosition;
	private double moveSpeed; // 移动速度（像素/帧）
	private boolean isMoving;
	private double tileSize;
	private CollisionDetector collisionDetector;
	private ItemManager itemManager;
	private List<Item> pickedUpItems; // 存储拾取的道具

	public MovementComponent(Point2D startPosition, double tileSize) {
		this.position = startPosition;
		this.targetPosition = startPosition;
		this.tileSize = tileSize;
		this.moveSpeed = tileSize * 0.2; // 每帧移动20%的格子距离，实现平滑移动
		this.isMoving = false;
		this.collisionDetector = null;
		this.itemManager = null;
		this.pickedUpItems = new java.util.ArrayList<>();
	}
	
	/**
	 * 设置碰撞检测器
	 * @param collisionDetector 碰撞检测器
	 */
	public void setCollisionDetector(CollisionDetector collisionDetector) {
		this.collisionDetector = collisionDetector;
	}
	
	/**
	 * 设置道具管理器
	 * @param itemManager 道具管理器
	 */
	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	public Point2D getPosition() { return position; }
	public Point2D getTargetPosition() { return targetPosition; }
	public boolean isMoving() { return isMoving; }
	public double getMoveSpeed() { return moveSpeed; }
	public void setMoveSpeed(double speed) { this.moveSpeed = speed; }

	// 设置目标位置（一个格子的距离）
	public void moveUp() { 
		if (!isMoving) {
			Point2D newTarget = position.add(0, -tileSize);
			if (canMoveTo(newTarget)) {
				targetPosition = newTarget;
				isMoving = true;
			}
		}
	}
	
	public void moveDown() { 
		if (!isMoving) {
			Point2D newTarget = position.add(0, tileSize);
			if (canMoveTo(newTarget)) {
				targetPosition = newTarget;
				isMoving = true;
			}
		}
	}
	
	public void moveLeft() { 
		if (!isMoving) {
			Point2D newTarget = position.add(-tileSize, 0);
			if (canMoveTo(newTarget)) {
				targetPosition = newTarget;
				isMoving = true;
			}
		}
	}
	
	public void moveRight() { 
		if (!isMoving) {
			Point2D newTarget = position.add(tileSize, 0);
			if (canMoveTo(newTarget)) {
				targetPosition = newTarget;
				isMoving = true;
			}
		}
	}
	
	/**
	 * 检查是否可以移动到目标位置
	 * @param targetPos 目标位置
	 * @return 是否可以移动
	 */
	private boolean canMoveTo(Point2D targetPos) {
		// 如果没有碰撞检测器，允许移动（向后兼容）
		if (collisionDetector == null) {
			return true;
		}
		
		// 检查目标位置是否可通行
		return collisionDetector.canMoveTo(position, targetPos);
	}

	// 更新移动状态（每帧调用）
	public void update() {
		if (isMoving) {
			// 计算到目标位置的距离
			double dx = targetPosition.getX() - position.getX();
			double dy = targetPosition.getY() - position.getY();
			double distance = Math.sqrt(dx * dx + dy * dy);
			
			if (distance <= moveSpeed) {
				// 已经接近目标位置，直接设置到目标位置
				position = targetPosition;
				isMoving = false;
			} else {
				// 向目标位置移动
				double moveX = (dx / distance) * moveSpeed;
				double moveY = (dy / distance) * moveSpeed;
				position = position.add(moveX, moveY);
			}
		}
		
		// 检查道具拾取
		checkItemPickup();
	}
	
	/**
	 * 检查道具拾取
	 */
	private void checkItemPickup() {
		if (itemManager != null) {
			double pickupRadius = tileSize * 0.6; // 拾取半径为格子大小的60%
			List<Item> newPickedUpItems = itemManager.checkItemPickup(position, pickupRadius);
			
			// 将新拾取的道具添加到列表中
			this.pickedUpItems.addAll(newPickedUpItems);
			
			// 这里可以添加拾取道具后的处理逻辑
			// 比如播放音效、显示提示等
			if (!newPickedUpItems.isEmpty()) {
				System.out.println("玩家拾取了 " + newPickedUpItems.size() + " 个道具");
			}
		}
	}
	
	/**
	 * 获取拾取的道具列表（供外部调用）
	 */
	public List<Item> getPickedUpItems() {
		List<Item> result = new java.util.ArrayList<>(pickedUpItems);
		pickedUpItems.clear(); // 清空列表，避免重复处理
		return result;
	}
}
