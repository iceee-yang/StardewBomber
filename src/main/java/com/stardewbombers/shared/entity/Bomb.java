package com.stardewbombers.shared.entity;

/**
 * 炸弹实体类
 * 负责炸弹的基本属性和状态管理
 */
public class Bomb {

    // 炸弹状态枚举
    public enum BombState {
        PLACED,     // 已放置
        TICKING,    // 倒计时中
        EXPLODING,  // 爆炸中
        EXPLODED    // 已爆炸
    }

    // 炸弹配置常量
    private static final double FUSE_TIME = com.stardewbombers.shared.util.GameConfig.BOMB_FUSE_TIME;        // 引信时间：3秒
    private static final double PLACEMENT_DELAY = 1.0; // 放置延迟：1秒
    private static final int DEFAULT_EXPLOSION_RADIUS = 1; // 默认爆炸范围：1格

    // 炸弹属性
    private int x, y;                    // 炸弹在网格中的位置
    private double worldX, worldY;       // 炸弹在世界坐标中的位置
    private BombState state;             // 炸弹状态
    private double fuseTime;             // 剩余引信时间
    private double totalFuseTime;        // 总引信时间
    private String ownerId;              // 炸弹拥有者ID
    private int explosionRadius;         // 爆炸范围（可配置）

    // 爆炸相关
    private boolean hasExploded;         // 是否已爆炸
    private boolean hasBeenAddedToExplodedList; // 是否已被添加到爆炸列表
    private long explosionStartTime;     // 爆炸开始时间
    private double explosionDuration;    // 爆炸持续时间

    public Bomb(int x, int y, String ownerId) {
        this.x = x;
        this.y = y;
        this.worldX = x * 40; // 网格大小为40，与TILE_SIZE保持一致
        this.worldY = y * 40;
        this.ownerId = ownerId;
        this.state = BombState.PLACED;
        this.totalFuseTime = FUSE_TIME;
        this.fuseTime = totalFuseTime;
        this.hasExploded = false;
        this.hasBeenAddedToExplodedList = false;
        this.explosionDuration = 2.0; // 爆炸持续2秒，与视觉效果保持一致
        this.explosionRadius = DEFAULT_EXPLOSION_RADIUS;
    }
    
    public Bomb(int x, int y, double worldX, double worldY, String ownerId) {
        this.x = x;
        this.y = y;
        this.worldX = worldX;
        this.worldY = worldY;
        this.ownerId = ownerId;
        this.state = BombState.PLACED;
        this.totalFuseTime = FUSE_TIME;
        this.fuseTime = totalFuseTime;
        this.hasExploded = false;
        this.hasBeenAddedToExplodedList = false;
        this.explosionDuration = 2.0; // 爆炸持续2秒，与视觉效果保持一致
        this.explosionRadius = DEFAULT_EXPLOSION_RADIUS;
    }

    /**
     * 获取炸弹颜色名称（用于调试或日志）
     */
    public String getBombColorName() {
        return "RED";
    }

    /**
     * 开始倒计时
     */
    public void startTicking() {
        if (state == BombState.PLACED) {
            state = BombState.TICKING;
        }
    }

    /**
     * 爆炸
     */
    public void explode() {
        // 一次性保护：防止重复爆炸
        if (state == BombState.EXPLODING || state == BombState.EXPLODED) {
            System.out.println("炸弹 " + ownerId + " 已经爆炸过，跳过重复爆炸 (当前状态: " + state + ")");
            return;
        }
        
        if (state != BombState.TICKING) {
            System.out.println("炸弹 " + ownerId + " 不在倒计时状态，跳过爆炸 (当前状态: " + state + ")");
            return;
        }
        
        System.out.println("炸弹 " + ownerId + " 开始爆炸 at " + System.currentTimeMillis());
        state = BombState.EXPLODING;
        explosionStartTime = System.currentTimeMillis();
    }

    /**
     * 获取爆炸进度（0.0 - 1.0）
     */
    public double getExplosionProgress() {
        if (state != BombState.EXPLODING) {
            return 0.0;
        }

        long currentTime = System.currentTimeMillis();
        double elapsed = (currentTime - explosionStartTime) / 1000.0;
        return Math.min(elapsed / explosionDuration, 1.0);
    }

    /**
     * 获取引信进度（0.0 - 1.0）
     */
    public double getFuseProgress() {
        if (state != BombState.TICKING) {
            return 0.0;
        }
        return 1.0 - (fuseTime / totalFuseTime);
    }

    /**
     * 检查炸弹是否可以爆炸
     */
    public boolean canExplode() {
        return state == BombState.TICKING && fuseTime <= 0;
    }

    /**
     * 检查炸弹是否已爆炸
     */
    public boolean isExploded() {
        return hasExploded;
    }

    /**
     * 检查炸弹是否正在爆炸
     */
    public boolean isExploding() {
        return state == BombState.EXPLODING;
    }

    // Getter 和 Setter 方法
    public int getX() { return x; }
    public int getY() { return y; }
    public double getWorldX() { return worldX; }
    public double getWorldY() { return worldY; }
    public BombState getState() { return state; }
    public double getFuseTime() { return fuseTime; }
    public double getTotalFuseTime() { return totalFuseTime; }
    public String getOwnerId() { return ownerId; }
    public int getExplosionRadius() { return explosionRadius; }
    public double getExplosionDuration() { return explosionDuration; }
    public long getExplosionStartTime() { return explosionStartTime; }
    public boolean isHasExploded() { return hasExploded; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setState(BombState state) { this.state = state; }
    public void setFuseTime(double fuseTime) { this.fuseTime = fuseTime; }
    public void setHasExploded(boolean hasExploded) { this.hasExploded = hasExploded; }
    public void setExplosionStartTime(long explosionStartTime) { this.explosionStartTime = explosionStartTime; }
    public void setExplosionRadius(int explosionRadius) { this.explosionRadius = explosionRadius; }

    /**
     * 更新炸弹位置（用于逻辑同步）
     */
    public void updatePosition(int gridX, int gridY) {
        this.x = gridX;
        this.y = gridY;
    }

    /**
     * 销毁炸弹
     */
    public void destroy() {
        state = BombState.EXPLODED;
        hasExploded = true;
    }
    
    /**
     * 检查炸弹是否已被添加到爆炸列表
     */
    public boolean hasBeenAddedToExplodedList() {
        return hasBeenAddedToExplodedList;
    }
    
    /**
     * 设置炸弹已被添加到爆炸列表
     */
    public void setHasBeenAddedToExplodedList(boolean hasBeenAdded) {
        this.hasBeenAddedToExplodedList = hasBeenAdded;
    }
}

