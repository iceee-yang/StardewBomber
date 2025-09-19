package com.stardewbombers.farm;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static final Map<String, Image> imageCache = new HashMap<>();
    
    // 图像路径常量 - 使用我们游戏中的资源路径
    private static final String IMAGE_PATH = "/textures/";
    private static final String FLOOR_IMAGE = IMAGE_PATH + "floor.png";
    
    // 种子图像 - 使用专门的种子图像
    private static final String CARROT_SEEDS = IMAGE_PATH + "Carrot_Seeds.png";
    private static final String STRAWBERRY_SEEDS = IMAGE_PATH + "Strawberry_Seeds.png";
    private static final String POTATO_SEEDS = IMAGE_PATH + "Potato_Seeds.png";
    
    // 果实图像 - 使用专门的果实图像
    private static final String CARROT = IMAGE_PATH + "Carrot.png";
    private static final String STRAWBERRY = IMAGE_PATH + "Strawberry.png";
    private static final String POTATO = IMAGE_PATH + "Potato.png";
    
    /**
     * 获取土地图像
     */
    public static Image getFloorImage() {
        return getImage(FLOOR_IMAGE);
    }
    
    /**
     * 根据作物类型和状态获取对应的图像
     */
    public static Image getCropImage(Farm.CropType cropType, Farm.CropState cropState) {
        System.out.println("getCropImage调用 - 类型: " + cropType + ", 状态: " + cropState);
        
        if (cropState == Farm.CropState.EMPTY || cropState == Farm.CropState.DEAD) {
            System.out.println("返回地板图像");
            return getFloorImage();
        }
        
        String imagePath;
        switch (cropType) {
            case CARROT:
                // 只有READY状态显示果实，其他状态显示种子
                imagePath = (cropState == Farm.CropState.READY) ? CARROT : CARROT_SEEDS;
                break;
            case STRAWBERRY:
                imagePath = (cropState == Farm.CropState.READY) ? STRAWBERRY : STRAWBERRY_SEEDS;
                break;
            case POTATO:
                imagePath = (cropState == Farm.CropState.READY) ? POTATO : POTATO_SEEDS;
                break;
            default:
                System.out.println("未知作物类型，返回地板图像");
                return getFloorImage();
        }
        
        System.out.println("选择的图像路径: " + imagePath);
        return getImage(imagePath);
    }
    
    /**
     * 获取种子图像
     */
    public static Image getSeedImage(Farm.CropType cropType) {
        String imagePath;
        switch (cropType) {
            case CARROT:
                imagePath = CARROT_SEEDS;
                break;
            case STRAWBERRY:
                imagePath = STRAWBERRY_SEEDS;
                break;
            case POTATO:
                imagePath = POTATO_SEEDS;
                break;
            default:
                return getFloorImage();
        }
        
        return getImage(imagePath);
    }
    
    /**
     * 获取果实图像
     */
    public static Image getFruitImage(Farm.CropType cropType) {
        String imagePath;
        switch (cropType) {
            case CARROT:
                imagePath = CARROT;
                break;
            case STRAWBERRY:
                imagePath = STRAWBERRY;
                break;
            case POTATO:
                imagePath = POTATO;
                break;
            default:
                return getFloorImage();
        }
        
        return getImage(imagePath);
    }
    
    /**
     * 获取图像，支持缓存
     */
    private static Image getImage(String imagePath) {
        if (imageCache.containsKey(imagePath)) {
            return imageCache.get(imagePath);
        }
        
        try {
            System.out.println("尝试加载图像: " + imagePath);
            InputStream imageStream = ImageManager.class.getResourceAsStream(imagePath);
            if (imageStream == null) {
                System.err.println("无法找到图像资源: " + imagePath);
                return null;
            }
            
            Image image = new Image(imageStream);
            if (image.isError()) {
                System.err.println("图像加载失败: " + imagePath + ", 错误: " + image.getException());
                return null;
            }
            
            System.out.println("成功加载图像: " + imagePath + ", 尺寸: " + image.getWidth() + "x" + image.getHeight());
            imageCache.put(imagePath, image);
            return image;
        } catch (Exception e) {
            System.err.println("加载图像失败: " + imagePath + ", 错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 清除图像缓存
     */
    public static void clearCache() {
        imageCache.clear();
    }
}