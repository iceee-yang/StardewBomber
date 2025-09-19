package com.stardewbombers.service;

import com.stardewbombers.model.Player;

public interface PlayerService {
    boolean register(String phone, String nickname, String rawPassword);
    boolean login(String phone, String rawPassword);

    Player getProfile(String phone);

    int getSeed1(String phone);
    int getSeed2(String phone);
    int getSeed3(String phone);
    int getCrop1(String phone);
    int getCrop2(String phone);
    int getCrop3(String phone);

    boolean addSeed1(String phone, int delta);
    boolean addSeed2(String phone, int delta);
    boolean addSeed3(String phone, int delta);
    boolean addCrop1(String phone, int delta);
    boolean addCrop2(String phone, int delta);
    boolean addCrop3(String phone, int delta);
}
