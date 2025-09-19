package com.stardewbombers.farm;

import java.time.LocalDateTime;

/**
 * 农场数据的序列化类
 */
public class FarmData {
    private int width;
    private int height;
    private String[][] crops; // 使用字符串数组存储枚举
    private String[][] states; // 使用字符串数组存储枚举
    private String[][] plantTimes; // 使用字符串数组存储时间
    private String[][] waterTimes; // 使用字符串数组存储时间
    
    public FarmData() {}
    
    public FarmData(Farm farm) {
        this.width = farm.getWidth();
        this.height = farm.getHeight();
        this.crops = new String[height][width];
        this.states = new String[height][width];
        this.plantTimes = new String[height][width];
        this.waterTimes = new String[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 序列化作物类型
                Farm.CropType cropType = farm.getCropType(x, y);
                this.crops[y][x] = cropType != null ? cropType.name() : null;
                
                // 序列化作物状态
                Farm.CropState cropState = farm.getCropState(x, y);
                this.states[y][x] = cropState != null ? cropState.name() : null;
                
                // 序列化种植时间
                LocalDateTime plantTime = farm.getPlantTime(x, y);
                this.plantTimes[y][x] = plantTime != null ? plantTime.toString() : null;
                
                // 序列化浇水时间
                LocalDateTime waterTime = farm.getWaterTime(x, y);
                this.waterTimes[y][x] = waterTime != null ? waterTime.toString() : null;
                
            }
        }
    }
    
    public Farm toFarm() {
        // 使用Farm类的静态方法从FarmData重建农场
        return Farm.fromFarmData(this);
    }
    
    // 简化的序列化方法，不使用Gson
    public String toJson() {
        // 简单的JSON字符串构建
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"width\":").append(width).append(",");
        json.append("\"height\":").append(height).append(",");
        json.append("\"crops\":").append(arrayToString(crops)).append(",");
        json.append("\"states\":").append(arrayToString(states)).append(",");
        json.append("\"plantTimes\":").append(arrayToString(plantTimes)).append(",");
        json.append("\"waterTimes\":").append(arrayToString(waterTimes));
        json.append("}");
        return json.toString();
    }
    
    private String arrayToString(String[][] array) {
        if (array == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("[");
            for (int j = 0; j < array[i].length; j++) {
                if (j > 0) sb.append(",");
                sb.append(array[i][j] != null ? "\"" + array[i][j] + "\"" : "null");
            }
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static FarmData fromJson(String json) {
        // 简化的JSON解析实现
        FarmData farmData = new FarmData();
        
        try {
            // 如果JSON为空或null，返回默认数据
            if (json == null || json.trim().isEmpty()) {
                return createDefaultFarmData();
            }
            
            // 移除JSON中的大括号和引号
            json = json.trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1);
            }
            
            // 如果JSON为空，返回默认数据
            if (json.trim().isEmpty()) {
                return createDefaultFarmData();
            }
            
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                if (pair.trim().isEmpty()) continue;
                
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length >= 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    
                    switch (key) {
                        case "width":
                            farmData.width = Integer.parseInt(value);
                            break;
                        case "height":
                            farmData.height = Integer.parseInt(value);
                            break;
                        case "crops":
                            farmData.crops = parseStringArray(value);
                            break;
                        case "states":
                            farmData.states = parseStringArray(value);
                            break;
                        case "plantTimes":
                            farmData.plantTimes = parseStringArray(value);
                            break;
                        case "waterTimes":
                            farmData.waterTimes = parseStringArray(value);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("解析JSON失败: " + e.getMessage());
            e.printStackTrace();
            // 返回默认的农场数据
            return createDefaultFarmData();
        }
        
        return farmData;
    }
    
    private static FarmData createDefaultFarmData() {
        FarmData farmData = new FarmData();
        farmData.width = 8;
        farmData.height = 6;
        farmData.crops = new String[6][8];
        farmData.states = new String[6][8];
        farmData.plantTimes = new String[6][8];
        farmData.waterTimes = new String[6][8];
        return farmData;
    }
    
    private static String[][] parseStringArray(String arrayStr) {
        // 简化的二维字符串数组解析
        if (arrayStr == null || arrayStr.equals("null") || arrayStr.equals("[]") || arrayStr.trim().isEmpty()) {
            return new String[6][8]; // 返回默认数组
        }
        
        try {
            // 移除方括号
            if (arrayStr.startsWith("[") && arrayStr.endsWith("]")) {
                arrayStr = arrayStr.substring(1, arrayStr.length() - 1);
            }
            
            // 如果内容为空，返回默认数组
            if (arrayStr.trim().isEmpty()) {
                return new String[6][8];
            }
            
            // 分割行 - 更安全的分割方式
            String[] rows = arrayStr.split("\\],\\[");
            String[][] result = new String[rows.length][];
            
            for (int i = 0; i < rows.length; i++) {
                String row = rows[i];
                // 移除行首尾的方括号
                if (row.startsWith("[")) row = row.substring(1);
                if (row.endsWith("]")) row = row.substring(0, row.length() - 1);
                
                if (row.trim().isEmpty()) {
                    result[i] = new String[8]; // 创建默认大小的数组
                } else {
                    String[] cols = row.split(",");
                    result[i] = new String[cols.length];
                    for (int j = 0; j < cols.length; j++) {
                        String col = cols[j].trim();
                        if (col.equals("null")) {
                            result[i][j] = null;
                        } else {
                            // 移除引号并处理可能的格式错误
                            String cleanCol = col.replace("\"", "");
                            // 处理可能的格式错误，如 "[NONE" -> "NONE"
                            if (cleanCol.startsWith("[") && !cleanCol.endsWith("]")) {
                                cleanCol = cleanCol.substring(1);
                            }
                            result[i][j] = cleanCol;
                        }
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("解析数组失败: " + e.getMessage());
            e.printStackTrace();
            return new String[6][8]; // 返回默认数组
        }
    }
    
    // Getters and Setters
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public String[][] getCrops() { return crops; }
    public void setCrops(String[][] crops) { this.crops = crops; }
    
    public String[][] getStates() { return states; }
    public void setStates(String[][] states) { this.states = states; }
    
    public String[][] getPlantTimes() { return plantTimes; }
    public void setPlantTimes(String[][] plantTimes) { this.plantTimes = plantTimes; }
    
    public String[][] getWaterTimes() { return waterTimes; }
    public void setWaterTimes(String[][] waterTimes) { this.waterTimes = waterTimes; }
}