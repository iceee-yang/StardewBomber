package com.stardewbombers.client;

import java.io.InputStream;
import javax.sound.sampled.*;

/**
 * 音乐管理器：处理游戏背景音乐和音效
 * 使用Java内置音频支持播放WAV文件
 */
public class MusicManager {
    private static boolean isMusicEnabled = true;
    private static boolean isSoundEnabled = true;
    private static double musicVolume = 0.7; // 默认音乐音量70%
    private static double soundVolume = 0.3; // 默认音效音量30%
    private static boolean isPlaying = false;
    private static String musicFilePath = null;
    private static String explosionSoundPath = null;
    private static Clip audioClip = null;
    private static String damageSoundPath = null;
    private static String speedupSoundPath = null;
    private static String healthUpSoundPath = null;
    
    /**
     * 初始化音乐管理器
     */
    public static void initialize() {
        try {
            // 使用类路径加载资源文件
            String musicResourcePath = "sounds/background_music.wav";
            String explosionResourcePath = "sounds/explosion.wav";
            String damageResourcePath = "music/wave/hurt.wav";
            String speedupResourcePath = "music/wave/speedup.wav";
            String healthUpResourcePath = "music/wave/health_up.wav";



            
            // 检查音乐文件是否存在
            InputStream musicStream = MusicManager.class.getClassLoader().getResourceAsStream(musicResourcePath);
            if (musicStream != null) {
                System.out.println("音乐文件找到: " + musicResourcePath);
                
                // 创建音频输入流
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicStream);
                
                // 获取音频格式
                AudioFormat format = audioInputStream.getFormat();
                
                // 创建数据行信息
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                
                // 创建音频剪辑
                audioClip = (Clip) AudioSystem.getLine(info);
                
                // 打开音频流
                audioClip.open(audioInputStream);
                
                // 设置循环播放
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
                
                // 设置初始音量
                setMusicVolume(musicVolume);
                
                System.out.println("音乐管理器初始化完成 - 支持实际音频播放");
                musicFilePath = musicResourcePath; // 保存资源路径用于日志
            } else {
                System.out.println("警告: 音乐文件不存在: " + musicResourcePath);
                musicFilePath = null;
            }
            
            // 检查音效文件是否存在
            InputStream explosionStream = MusicManager.class.getClassLoader().getResourceAsStream(explosionResourcePath);
            if (explosionStream != null) {
                System.out.println("爆炸音效文件找到: " + explosionResourcePath);
                explosionSoundPath = explosionResourcePath; // 保存资源路径用于日志
                explosionStream.close(); // 关闭测试流
            } else {
                System.out.println("警告: 爆炸音效文件不存在: " + explosionResourcePath);
                explosionSoundPath = null;
            }

            // 检查受伤音效文件是否存在
            InputStream damageStream = MusicManager.class.getClassLoader().getResourceAsStream(damageResourcePath);
            if (damageStream != null) {
                System.out.println("受伤音效文件找到: " + damageResourcePath);
                damageSoundPath = damageResourcePath; // 保存资源路径用于日志
                damageStream.close(); // 关闭测试流
            } else {
                System.out.println("警告: 受伤音效文件不存在: " + damageResourcePath);
                damageSoundPath = null;
            }
            
            // 检查加速音效文件是否存在
            InputStream speedupStream = MusicManager.class.getClassLoader().getResourceAsStream(speedupResourcePath);
            if (speedupStream != null) {
                System.out.println("加速音效文件找到: " + speedupResourcePath);
                speedupSoundPath = speedupResourcePath; // 保存资源路径用于日志
                speedupStream.close(); // 关闭测试流
            } else {
                System.out.println("警告: 加速音效文件不存在: " + speedupResourcePath);
                speedupSoundPath = null;
            }

            // 检查药水音效文件是否存在
            InputStream healthUpStream = MusicManager.class.getClassLoader().getResourceAsStream(healthUpResourcePath);
            if (healthUpStream != null) {
                System.out.println("药水音效文件找到: " + healthUpResourcePath);
                healthUpSoundPath = healthUpResourcePath; // 保存资源路径用于日志
                healthUpStream.close(); // 关闭测试流
            } else {
                System.out.println("警告: 药水音效文件不存在: " + healthUpResourcePath);
                healthUpSoundPath = null;
            }
            
        } catch (Exception e) {
            System.out.println("音乐管理器初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 开始播放背景音乐
     */
    public static void startBackgroundMusic() {
        if (!isMusicEnabled) {
            System.out.println("音乐已禁用，不播放背景音乐");
            return;
        }
        
        if (audioClip != null) {
            try {
                audioClip.start();
                isPlaying = true;
                System.out.println("🎵 背景音乐开始播放: " + musicFilePath);
                System.out.println("   音量: " + (int)(musicVolume * 100) + "%");
                System.out.println("   状态: 循环播放中...");
            } catch (Exception e) {
                System.out.println("播放音乐时出错: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("无法播放音乐：音频剪辑未初始化");
        }
    }
    
    /**
     * 停止播放背景音乐
     */
    public static void stopBackgroundMusic() {
        if (audioClip != null && isPlaying) {
            try {
                audioClip.stop();
                isPlaying = false;
                System.out.println("🔇 背景音乐停止播放");
            } catch (Exception e) {
                System.out.println("停止音乐时出错: " + e.getMessage());
            }
        }
    }
    
    /**
     * 暂停背景音乐
     */
    public static void pauseBackgroundMusic() {
        if (audioClip != null && isPlaying) {
            try {
                audioClip.stop();
                isPlaying = false;
                System.out.println("⏸️ 背景音乐暂停");
            } catch (Exception e) {
                System.out.println("暂停音乐时出错: " + e.getMessage());
            }
        }
    }
    
    /**
     * 恢复背景音乐
     */
    public static void resumeBackgroundMusic() {
        if (isMusicEnabled && audioClip != null) {
            try {
                audioClip.start();
                isPlaying = true;
                System.out.println("▶️ 背景音乐恢复播放");
            } catch (Exception e) {
                System.out.println("恢复音乐时出错: " + e.getMessage());
            }
        }
    }
    
    /**
     * 播放爆炸音效
     */
    public static void playExplosionSound() {
        if (!isSoundEnabled || explosionSoundPath == null) {
            return;
        }
        
        try {
            // 在新线程中播放音效，避免阻塞游戏主线程
            new Thread(() -> {
                try {
                    // 使用类路径加载音效文件
                    InputStream soundStream = MusicManager.class.getClassLoader().getResourceAsStream(explosionSoundPath);
                    if (soundStream == null) {
                        System.out.println("无法加载爆炸音效文件: " + explosionSoundPath);
                        return;
                    }
                    
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundStream);
                    AudioFormat format = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip soundClip = (Clip) AudioSystem.getLine(info);
                    
                    soundClip.open(audioInputStream);
                    
                    // 设置音效音量
                    if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(soundVolume) / Math.log(10.0) * 20.0);
                        volumeControl.setValue(dB);
                    }
                    
                    soundClip.start();
                    
                    // 等待音效播放完成
                    soundClip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            soundClip.close();
                        }
                    });
                    
                } catch (Exception e) {
                    System.out.println("播放爆炸音效时出错: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.out.println("创建爆炸音效播放线程时出错: " + e.getMessage());
        }
    }
    /**
     * 播放受伤音效（延迟0.8秒播放）
     */
    public static void playDamageSound() {
        if (!isSoundEnabled || damageSoundPath == null) {
            return;
        }
        
        try {
            // 在新线程中播放音效，避免阻塞游戏主线程
            new Thread(() -> {
                try {
                    // 延迟0.8秒播放受伤音效
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    System.out.println("受伤音效延迟被中断: " + e.getMessage());
                    return;
                }
                try {
                    // 使用类路径加载音效文件
                    InputStream soundStream = MusicManager.class.getClassLoader().getResourceAsStream(damageSoundPath);
                    if (soundStream == null) {
                        System.out.println("无法加载受伤音效文件: " + damageSoundPath);
                        return;
                    }
                    
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundStream);
                    AudioFormat format = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip soundClip = (Clip) AudioSystem.getLine(info);
                    
                    soundClip.open(audioInputStream);
                    
                    // 设置音效音量 - 使用用户设置的音效音量
                    if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(soundVolume) / Math.log(10.0) * 20.0);
                        volumeControl.setValue(dB);
                    }
                    
                    soundClip.start();
                    
                    // 等待音效播放完成
                    soundClip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            soundClip.close();
                        }
                    });
                    
                } catch (Exception e) {
                    System.out.println("播放受伤音效时出错: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.out.println("创建受伤音效播放线程时出错: " + e.getMessage());
        }
    }
    
    /**
     * 播放加速音效（捡靴子时）
     */
    public static void playSpeedupSound() {
        if (!isSoundEnabled || speedupSoundPath == null) {
            return;
        }
        
        try {
            // 在新线程中播放音效，避免阻塞游戏主线程
            new Thread(() -> {
                try {
                    // 使用类路径加载音效文件
                    InputStream soundStream = MusicManager.class.getClassLoader().getResourceAsStream(speedupSoundPath);
                    if (soundStream == null) {
                        System.out.println("无法加载加速音效文件: " + speedupSoundPath);
                        return;
                    }
                    
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundStream);
                    AudioFormat format = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip soundClip = (Clip) AudioSystem.getLine(info);
                    
                    soundClip.open(audioInputStream);
                    
                    // 设置音效音量 - 使用用户设置的音效音量
                    if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(soundVolume) / Math.log(10.0) * 20.0);
                        volumeControl.setValue(dB);
                    }
                    
                    soundClip.start();
                    
                    // 等待音效播放完成
                    soundClip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            soundClip.close();
                        }
                    });
                    
                } catch (Exception e) {
                    System.out.println("播放加速音效时出错: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.out.println("创建加速音效播放线程时出错: " + e.getMessage());
        }
    }
    
    /**
     * 播放药水音效
     */
    public static void playHealthUpSound() {
        System.out.println("尝试播放药水音效 - 音效启用: " + isSoundEnabled + ", 路径: " + healthUpSoundPath);
        if (!isSoundEnabled || healthUpSoundPath == null) {
            System.out.println("药水音效播放被跳过 - 音效禁用或路径为空");
            return;
        }
        try {
            // 在新线程中播放音效，避免阻塞游戏主线程
            new Thread(() -> {
                try {
                    // 使用类路径加载音效文件
                    InputStream soundStream = MusicManager.class.getClassLoader().getResourceAsStream(healthUpSoundPath);
                    if (soundStream == null) {
                        System.out.println("无法加载药水音效文件: " + healthUpSoundPath);
                        return;
                    }
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundStream);
                    AudioFormat format = audioInputStream.getFormat();
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    Clip soundClip = (Clip) AudioSystem.getLine(info);
                    soundClip.open(audioInputStream);
                    // 设置音效音量 - 使用用户设置的音效音量
                    if (soundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB = (float) (Math.log(soundVolume) / Math.log(10.0) * 20.0);
                        volumeControl.setValue(dB);
                    }
                    soundClip.start();
                    soundClip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            soundClip.close();
                        }
                    });
                } catch (Exception e) {
                    System.out.println("播放药水音效时出错: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.out.println("创建药水音效播放线程时出错: " + e.getMessage());
        }
    }



    /**
     * 设置音乐音量
     * @param newVolume 音量值 (0.0 - 1.0)
     */
    public static void setMusicVolume(double newVolume) {
        musicVolume = Math.max(0.0, Math.min(1.0, newVolume)); // 限制在0-1之间
        
        // 更新音频剪辑的音量
        if (audioClip != null) {
            try {
                FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(musicVolume) / Math.log(10.0) * 20.0);
                volumeControl.setValue(dB);
            } catch (Exception e) {
                System.out.println("设置音乐音量时出错: " + e.getMessage());
            }
        }
        
        System.out.println("🔊 音乐音量设置为: " + (int)(musicVolume * 100) + "%");
    }
    
    /**
     * 设置音效音量
     * @param newVolume 音量值 (0.0 - 1.0)
     */
    public static void setSoundVolume(double newVolume) {
        soundVolume = Math.max(0.0, Math.min(1.0, newVolume)); // 限制在0-1之间
        System.out.println("🔊 音效音量设置为: " + (int)(soundVolume * 100) + "%");
    }
    
    /**
     * 获取当前音乐音量
     */
    public static double getMusicVolume() {
        return musicVolume;
    }
    
    /**
     * 获取当前音效音量
     */
    public static double getSoundVolume() {
        return soundVolume;
    }
    
    /**
     * 启用/禁用音乐
     */
    public static void setMusicEnabled(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        } else if (audioClip != null) {
            startBackgroundMusic();
        }
        System.out.println("🎵 音乐" + (enabled ? "启用" : "禁用"));
    }
    
    /**
     * 启用/禁用音效
     */
    public static void setSoundEnabled(boolean enabled) {
        isSoundEnabled = enabled;
        System.out.println("🔊 音效" + (enabled ? "启用" : "禁用"));
    }
    
    /**
     * 检查音效是否启用
     */
    public static boolean isSoundEnabled() {
        return isSoundEnabled;
    }
    
    /**
     * 检查音乐是否启用
     */
    public static boolean isMusicEnabled() {
        return isMusicEnabled;
    }
    
    /**
     * 检查背景音乐是否正在播放
     */
    public static boolean isBackgroundMusicPlaying() {
        return isPlaying && isMusicEnabled && audioClip != null;
    }
    
    /**
     * 获取音乐状态信息
     */
    public static String getMusicStatus() {
        if (!isMusicEnabled) {
            return "音乐已禁用";
        } else if (isPlaying) {
            return "音乐播放中 (音量: " + (int)(musicVolume * 100) + "%)";
        } else {
            return "音乐已停止";
        }
    }
    
    /**
     * 获取音效状态信息
     */
    public static String getSoundStatus() {
        if (!isSoundEnabled) {
            return "音效已禁用";
        } else {
            return "音效启用 (音量: " + (int)(soundVolume * 100) + "%)";
        }
    }
    
    /**
     * 清理资源
     */
    public static void cleanup() {
        if (audioClip != null) {
            try {
                if (audioClip.isRunning()) {
                    audioClip.stop();
                }
                audioClip.close();
                audioClip = null;
                System.out.println("🔇 音频剪辑已停止并关闭");
            } catch (Exception e) {
                System.out.println("清理音乐资源时出错: " + e.getMessage());
            }
        }
        isPlaying = false;
        isMusicEnabled = false;
        isSoundEnabled = false;
        System.out.println("🧹 音乐管理器资源已清理");
    }
}