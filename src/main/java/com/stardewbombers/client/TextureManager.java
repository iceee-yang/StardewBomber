package com.stardewbombers.client;

import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.enums.PowerUpType;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 纹理管理器：从 resources/textures/ 加载对应的PNG图片
 * 支持同一类型的多变体（例如 bush1/bush2/bush3 ）
 */
public final class TextureManager {
    private static final String TEXTURE_DIR = "textures/";
    private static final Map<BlockType, List<Image>> blockTypeToImages = new EnumMap<>(BlockType.class);
    private static final Map<PowerUpType, Image> itemTypeToImages = new EnumMap<>(PowerUpType.class);
    private static final Random random = new Random();
    private static Image bombImage = null;

    private TextureManager() {}

    public static void initialize(int expectedTileWidth, int expectedTileHeight) {
        // 单纹理元素
        loadSingle(BlockType.PUMPKIN, "pumpkin.png");
        loadSingle(BlockType.MELON, "melon.png");
        loadSingle(BlockType.STUMP, "stump.png");

        // Bush 三种变体
        loadMultiple(BlockType.BUSHES, "bush1.png", "bush2.png", "bush3.png");

        // 新添加的家具类纹理（注意文件名大小写）
        loadSingle(BlockType.CABINET1, "cabinet1.png");
        loadSingle(BlockType.CABINET2, "cabinet2.png");
        loadSingle(BlockType.TABLE, "Table.png");
        loadSingle(BlockType.STOOL, "Stool.png");
        loadSingle(BlockType.CHAIR, "Chair.png");
        loadSingle(BlockType.FIREPLACE1, "Fireplace1.png");
        loadSingle(BlockType.FIREPLACE2, "Fireplace2.png");
        loadSingle(BlockType.RUG, "rug.png");

        // 可选：floor.png（用于未来需要渲染地板时）
        loadOptional(BlockType.FLOOR, "floor.png");

        // 洞穴元素纹理
        loadSingle(BlockType.GHOST, "Ghost.png");
        loadSingle(BlockType.SKELTON, "Skeleton.png");
        loadSingle(BlockType.MUMMY, "Mummy.png");
        loadSingle(BlockType.DIRETFLOOR, "DiretFloor.png");
        loadSingle(BlockType.BIXITE, "Bixite.png");
        loadSingle(BlockType.QUARTZ, "Quartz.png");
        loadSingle(BlockType.MUSHROOM1, "Mushroom1.png");
        loadSingle(BlockType.MUSHROOM2, "Mushroom2.png");

        // 加载炸弹图像
        bombImage = loadImage("bomb.png");

        // 加载道具图像
        loadItemImage(PowerUpType.BOOTS, "boots.png");
        loadItemImage(PowerUpType.LIFE_ELIXIR, "life_elixir.png");

        // 可按需校验尺寸（非强制）
        // 我们不强制缩放/校验，这在视图层通过 ImageView 的 fitWidth/fitHeight 控制
    }

    public static Image getImage(BlockType type) {
        List<Image> images = blockTypeToImages.get(type);
        if (images == null || images.isEmpty()) {
            return null;
        }
        if (images.size() == 1) {
            return images.get(0);
        }
        return images.get(random.nextInt(images.size()));
    }
    
    /**
     * 获取炸弹图像
     */
    public static Image getBombImage() {
        return bombImage;
    }
    
    /**
     * 获取道具图像
     */
    public static Image getItemImage(PowerUpType type) {
        Image image = itemTypeToImages.get(type);
        if (image == null) {
            System.out.println("警告: 未找到道具图像 " + type);
        }
        return image;
    }

    private static void loadSingle(BlockType type, String fileName) {
        Image image = loadImage(fileName);
        if (image != null) {
            List<Image> list = new ArrayList<>();
            list.add(image);
            blockTypeToImages.put(type, list);
        }
    }

    private static void loadMultiple(BlockType type, String... fileNames) {
        List<Image> list = new ArrayList<>();
        for (String fileName : fileNames) {
            Image image = loadImage(fileName);
            if (image != null) {
                list.add(image);
            }
        }
        if (!list.isEmpty()) {
            blockTypeToImages.put(type, list);
        }
    }

    private static void loadOptional(BlockType type, String fileName) {
        Image image = loadImage(fileName);
        if (image != null) {
            List<Image> list = new ArrayList<>();
            list.add(image);
            blockTypeToImages.put(type, list);
        }
    }

    private static void loadItemImage(PowerUpType type, String fileName) {
        Image image = loadImage(fileName);
        if (image != null) {
            itemTypeToImages.put(type, image);
            System.out.println("成功加载道具图像: " + type + " -> " + fileName);
        } else {
            System.out.println("失败加载道具图像: " + type + " -> " + fileName);
        }
    }

    private static Image loadImage(String fileName) {
        String path = TEXTURE_DIR + fileName;
        InputStream is = TextureManager.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            System.out.println("警告: 未找到纹理文件 " + path);
            return null;
        }
        return new Image(is);
    }
}


