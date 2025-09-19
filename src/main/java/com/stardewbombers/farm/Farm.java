package com.stardewbombers.farm;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Farm {
    public enum CropType {
        NONE, CARROT, STRAWBERRY, POTATO
    }
    
    public enum CropState {
        EMPTY,      // 空地
        PLANTED,    // 已种植（显示种子图像）
        WATERED,    // 已浇水（种子已浇水，等待成熟）
        READY,      // 可收获（显示果实图像）
        DEAD        // 枯死（超过一天未浇水）
    }
    
    private final int width;
    private final int height;
    private final CropType[][] crops;
    private final CropState[][] states;
    private final LocalDateTime[][] plantTimes;
    private final LocalDateTime[][] waterTimes; // 浇水时间
    private final long growTimeMinutes = 5; // 5分钟成熟
    private final long deathTimeHours = 24; // 24小时未浇水会枯死
    
    public Farm(int width, int height) {
        this.width = width;
        this.height = height;
        this.crops = new CropType[height][width];
        this.states = new CropState[height][width];
        this.plantTimes = new LocalDateTime[height][width];
        this.waterTimes = new LocalDateTime[height][width];
        
        // 初始化所有格子为空地
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                crops[y][x] = CropType.NONE;
                states[y][x] = CropState.EMPTY;
                plantTimes[y][x] = null;
                waterTimes[y][x] = null;
            }
        }
    }
    
    public boolean canPlant(int x, int y) {
        return isValidPosition(x, y) && states[y][x] == CropState.EMPTY;
    }
    
    public boolean plantCrop(int x, int y, CropType cropType) {
        if (canPlant(x, y)) {
            crops[y][x] = cropType;
            states[y][x] = CropState.PLANTED;
            plantTimes[y][x] = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    public boolean canWater(int x, int y) {
        if (!isValidPosition(x, y)) return false;
        return states[y][x] == CropState.PLANTED;
    }
    
    public boolean waterCrop(int x, int y) {
        if (canWater(x, y)) {
            states[y][x] = CropState.WATERED;
            waterTimes[y][x] = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    public boolean canHarvest(int x, int y) {
        if (!isValidPosition(x, y)) return false;
        return states[y][x] == CropState.READY;
    }
    
    public CropType harvestCrop(int x, int y) {
        if (canHarvest(x, y)) {
            CropType cropType = crops[y][x];
            crops[y][x] = CropType.NONE;
            states[y][x] = CropState.EMPTY;
            plantTimes[y][x] = null;
            waterTimes[y][x] = null;
            return cropType;
        }
        return CropType.NONE;
    }
    
    public void updateCropStates() {
        LocalDateTime now = LocalDateTime.now();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (states[y][x] == CropState.PLANTED && plantTimes[y][x] != null) {
                    // 检查是否超过24小时未浇水
                    long hoursSincePlanted = java.time.Duration.between(plantTimes[y][x], now).toHours();
                    if (hoursSincePlanted >= deathTimeHours) {
                        states[y][x] = CropState.DEAD;
                        crops[y][x] = CropType.NONE;
                        plantTimes[y][x] = null;
                        waterTimes[y][x] = null;
                    }
                } else if (states[y][x] == CropState.WATERED && waterTimes[y][x] != null) {
                    // 检查是否已成熟
                    long minutesSinceWatered = java.time.Duration.between(waterTimes[y][x], now).toMinutes();
                    if (minutesSinceWatered >= growTimeMinutes) {
                        states[y][x] = CropState.READY;
                    }
                }
            }
        }
    }
    
    public CropType getCropType(int x, int y) {
        if (isValidPosition(x, y)) {
            return crops[y][x];
        }
        return CropType.NONE;
    }
    
    public CropState getCropState(int x, int y) {
        if (isValidPosition(x, y)) {
            return states[y][x];
        }
        return CropState.EMPTY;
    }
    
    public LocalDateTime getPlantTime(int x, int y) {
        if (isValidPosition(x, y)) {
            return plantTimes[y][x];
        }
        return null;
    }
    
    public LocalDateTime getWaterTime(int x, int y) {
        if (isValidPosition(x, y)) {
            return waterTimes[y][x];
        }
        return null;
    }
    
    public long getRemainingGrowTime(int x, int y) {
        if (!isValidPosition(x, y) || states[y][x] != CropState.WATERED || waterTimes[y][x] == null) {
            return 0;
        }
        
        long minutesPassed = java.time.Duration.between(waterTimes[y][x], LocalDateTime.now()).toMinutes();
        long remaining = growTimeMinutes - minutesPassed;
        return Math.max(0, remaining);
    }
    
    public long getRemainingDeathTime(int x, int y) {
        if (!isValidPosition(x, y) || states[y][x] != CropState.PLANTED || plantTimes[y][x] == null) {
            return 0;
        }
        
        long hoursPassed = java.time.Duration.between(plantTimes[y][x], LocalDateTime.now()).toHours();
        long remaining = deathTimeHours - hoursPassed;
        return Math.max(0, remaining);
    }
    
    public List<Position> getReadyCrops() {
        List<Position> readyCrops = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (states[y][x] == CropState.READY) {
                    readyCrops.add(new Position(x, y));
                }
            }
        }
        return readyCrops;
    }
    
    public int getReadyCropCount(CropType cropType) {
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (states[y][x] == CropState.READY && crops[y][x] == cropType) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    /**
     * 设置作物类型（用于从数据重建）
     */
    public void setCropType(int x, int y, CropType cropType) {
        if (isValidPosition(x, y)) {
            crops[y][x] = cropType;
        }
    }
    
    /**
     * 设置作物状态（用于从数据重建）
     */
    public void setCropState(int x, int y, CropState cropState) {
        if (isValidPosition(x, y)) {
            states[y][x] = cropState;
        }
    }
    
    /**
     * 设置种植时间（用于从数据重建）
     */
    public void setPlantTime(int x, int y, LocalDateTime plantTime) {
        if (isValidPosition(x, y)) {
            plantTimes[y][x] = plantTime;
        }
    }
    
    /**
     * 设置浇水时间（用于从数据重建）
     */
    public void setWaterTime(int x, int y, LocalDateTime waterTime) {
        if (isValidPosition(x, y)) {
            waterTimes[y][x] = waterTime;
        }
    }
    
    /**
     * 从FarmData重建农场状态
     */
    public static Farm fromFarmData(FarmData farmData) {
        if (farmData == null) {
            return new Farm(8, 6); // 返回默认农场
        }
        
        Farm farm = new Farm(farmData.getWidth(), farmData.getHeight());
        
        // 安全地访问数组，添加边界检查
        String[][] crops = farmData.getCrops();
        String[][] states = farmData.getStates();
        String[][] plantTimes = farmData.getPlantTimes();
        String[][] waterTimes = farmData.getWaterTimes();
        
        // 如果数组为null，创建默认数组
        if (crops == null) crops = new String[farmData.getHeight()][farmData.getWidth()];
        if (states == null) states = new String[farmData.getHeight()][farmData.getWidth()];
        if (plantTimes == null) plantTimes = new String[farmData.getHeight()][farmData.getWidth()];
        if (waterTimes == null) waterTimes = new String[farmData.getHeight()][farmData.getWidth()];
        
        for (int y = 0; y < farmData.getHeight(); y++) {
            for (int x = 0; x < farmData.getWidth(); x++) {
                // 重建作物类型 - 添加更安全的边界检查
                String cropTypeStr = null;
                if (crops != null && y < crops.length && crops[y] != null && x < crops[y].length) {
                    cropTypeStr = crops[y][x];
                }
                
                if (cropTypeStr != null && !cropTypeStr.equals("NONE")) {
                    try {
                        CropType cropType = CropType.valueOf(cropTypeStr);
                        farm.setCropType(x, y, cropType);
                    } catch (IllegalArgumentException e) {
                        farm.setCropType(x, y, CropType.NONE);
                    }
                } else {
                    // 确保NONE类型也被正确设置
                    farm.setCropType(x, y, CropType.NONE);
                }
                
                // 重建作物状态 - 添加更安全的边界检查
                String cropStateStr = null;
                if (states != null && y < states.length && states[y] != null && x < states[y].length) {
                    cropStateStr = states[y][x];
                }
                
                if (cropStateStr != null) {
                    try {
                        CropState cropState = CropState.valueOf(cropStateStr);
                        farm.setCropState(x, y, cropState);
                    } catch (IllegalArgumentException e) {
                        farm.setCropState(x, y, CropState.EMPTY);
                    }
                } else {
                    // 确保状态也被正确设置
                    farm.setCropState(x, y, CropState.EMPTY);
                }
                
                // 重建种植时间 - 添加更安全的边界检查
                String plantTimeStr = null;
                if (plantTimes != null && y < plantTimes.length && plantTimes[y] != null && x < plantTimes[y].length) {
                    plantTimeStr = plantTimes[y][x];
                }
                
                if (plantTimeStr != null && !plantTimeStr.equals("null")) {
                    try {
                        LocalDateTime plantTime = LocalDateTime.parse(plantTimeStr);
                        farm.setPlantTime(x, y, plantTime);
                    } catch (Exception e) {
                        farm.setPlantTime(x, y, null);
                    }
                }
                
                // 重建浇水时间 - 添加更安全的边界检查
                String waterTimeStr = null;
                if (waterTimes != null && y < waterTimes.length && waterTimes[y] != null && x < waterTimes[y].length) {
                    waterTimeStr = waterTimes[y][x];
                }
                
                if (waterTimeStr != null && !waterTimeStr.equals("null")) {
                    try {
                        LocalDateTime waterTime = LocalDateTime.parse(waterTimeStr);
                        farm.setWaterTime(x, y, waterTime);
                    } catch (Exception e) {
                        farm.setWaterTime(x, y, null);
                    }
                }
            }
        }
        
        return farm;
    }
}