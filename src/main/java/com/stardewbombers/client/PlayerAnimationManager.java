package com.stardewbombers.client;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 玩家动画管理器
 * 管理角色的行走动画，支持4个方向，每个方向4帧动画
 */
public class PlayerAnimationManager {
    
    public enum Direction {
        DOWN(0), RIGHT(1), UP(2), LEFT(3);
        
        private final int row;
        
        Direction(int row) {
            this.row = row;
        }
        
        public int getRow() {
            return row;
        }
    }
    
    public enum CharacterType {
        ALEX("Alex.png"),
        ABIGAIL("Abigail.png"),
        HALEY("Haley.png"),
        LEWIS("Lewis.png");
        
        private final String fileName;
        
        CharacterType(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFileName() {
            return fileName;
        }
    }
    
    private static final int FRAMES_PER_DIRECTION = 4;
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 32;
    
    private final Map<CharacterType, Image> characterImages = new HashMap<>();
    private final Map<CharacterType, ImageView> characterViews = new HashMap<>();
    
    private CharacterType currentCharacter = CharacterType.ALEX;
    private Direction currentDirection = Direction.DOWN;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION = 200; // 每帧200毫秒
    
    public PlayerAnimationManager() {
        loadCharacterImages();
    }
    
    private void loadCharacterImages() {
        for (CharacterType character : CharacterType.values()) {
            String path = "character(16-32)/" + character.getFileName();
            InputStream is = PlayerAnimationManager.class.getClassLoader().getResourceAsStream(path);
            if (is != null) {
                Image image = new Image(is);
                characterImages.put(character, image);
                
                // 创建ImageView
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(FRAME_WIDTH);
                imageView.setFitHeight(FRAME_HEIGHT);
                imageView.setPreserveRatio(false);
                characterViews.put(character, imageView);
            } else {
                System.out.println("警告: 未找到角色图像文件 " + path);
            }
        }
    }
    
    public ImageView getCurrentFrameView() {
        ImageView imageView = characterViews.get(currentCharacter);
        if (imageView != null) {
            updateFrame();
            setFrameViewport(imageView);
        }
        return imageView;
    }
    
    
    private void setFrameViewport(ImageView imageView) {
        int sourceX = currentFrame * FRAME_WIDTH;
        int sourceY = currentDirection.getRow() * FRAME_HEIGHT;
        
        imageView.setViewport(new javafx.geometry.Rectangle2D(
            sourceX, sourceY, FRAME_WIDTH, FRAME_HEIGHT
        ));
    }
    
    public void setDirection(Direction direction) {
        if (this.currentDirection != direction) {
            this.currentDirection = direction;
            this.currentFrame = 0; // 重置动画帧
        }
    }
    
    public void setCharacter(CharacterType character) {
        this.currentCharacter = character;
    }
    
    public Direction getCurrentDirection() {
        return currentDirection;
    }
    
    public CharacterType getCurrentCharacter() {
        return currentCharacter;
    }
    
    public void resetAnimation() {
        currentFrame = 0;
        lastFrameTime = System.currentTimeMillis();
    }
    
    public void updateFrame() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % FRAMES_PER_DIRECTION;
            lastFrameTime = currentTime;
        }
    }
    
    public int getCurrentFrame() {
        return currentFrame;
    }
}
