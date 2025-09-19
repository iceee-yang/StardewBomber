package com.stardewbombers.model;

public class Player {
    private String phone;
    private String nickname;
    
    // 游戏位置信息
    private int x;
    private int y;

    private int seed1;
    private int seed2;
    private int seed3;
    private int crop1;
    private int crop2;
    private int crop3;

    public Player(String phone, String nickname) {
        this.phone = phone;
        this.nickname = nickname;
    }

    public String getPhone() { return phone; }
    public String getNickname() { return nickname; }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public int getSeed1() { return seed1; }
    public int getSeed2() { return seed2; }
    public int getSeed3() { return seed3; }
    public int getCrop1() { return crop1; }
    public int getCrop2() { return crop2; }
    public int getCrop3() { return crop3; }

    public void setSeed1(int v) { this.seed1 = v; }
    public void setSeed2(int v) { this.seed2 = v; }
    public void setSeed3(int v) { this.seed3 = v; }
    public void setCrop1(int v) { this.crop1 = v; }
    public void setCrop2(int v) { this.crop2 = v; }
    public void setCrop3(int v) { this.crop3 = v; }
}
