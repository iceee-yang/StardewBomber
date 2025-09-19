package com.stardewbombers.service.impl;

import com.stardewbombers.db.Database;
import com.stardewbombers.model.Player;
import com.stardewbombers.service.PlayerService;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerServiceImpl implements PlayerService {
    @Override
    public boolean register(String phone, String nickname, String rawPassword) {
        String sql = "INSERT INTO players (phone, nickname, password_hash, seed1, seed2, seed3) VALUES (?, ?, ?, ?, ?, ?)";
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, nickname);
            ps.setString(3, hash);
            ps.setInt(4, 3); // 给新玩家5个胡萝卜种子
            ps.setInt(5, 3); // 给新玩家5个草莓种子
            ps.setInt(6, 3); // 给新玩家5个土豆种子
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean login(String phone, String rawPassword) {
        String sql = "SELECT password_hash FROM players WHERE phone=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            String hash = rs.getString(1);
            return BCrypt.checkpw(rawPassword, hash);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Player getProfile(String phone) {
        String sql = "SELECT phone,nickname,seed1,seed2,seed3,crop1,crop2,crop3 FROM players WHERE phone=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            Player p = new Player(rs.getString("phone"), rs.getString("nickname"));
            p.setSeed1(rs.getInt("seed1"));
            p.setSeed2(rs.getInt("seed2"));
            p.setSeed3(rs.getInt("seed3"));
            p.setCrop1(rs.getInt("crop1"));
            p.setCrop2(rs.getInt("crop2"));
            p.setCrop3(rs.getInt("crop3"));
            return p;
        } catch (SQLException e) {
            return null;
        }
    }

    private Integer getIntField(String phone, String field) {
        String sql = "SELECT " + field + " FROM players WHERE phone=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            return rs.getInt(1);
        } catch (SQLException e) {
            return null;
        }
    }

    private boolean addIntField(String phone, String field, int delta) {
        String sql = "UPDATE players SET " + field + " = " + field + " + ? WHERE phone=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override public int getSeed1(String phone) { return safeInt(getIntField(phone, "seed1")); }
    @Override public int getSeed2(String phone) { return safeInt(getIntField(phone, "seed2")); }
    @Override public int getSeed3(String phone) { return safeInt(getIntField(phone, "seed3")); }
    @Override public int getCrop1(String phone) { return safeInt(getIntField(phone, "crop1")); }
    @Override public int getCrop2(String phone) { return safeInt(getIntField(phone, "crop2")); }
    @Override public int getCrop3(String phone) { return safeInt(getIntField(phone, "crop3")); }

    @Override public boolean addSeed1(String phone, int delta) { return addIntField(phone, "seed1", delta); }
    @Override public boolean addSeed2(String phone, int delta) { return addIntField(phone, "seed2", delta); }
    @Override public boolean addSeed3(String phone, int delta) { return addIntField(phone, "seed3", delta); }
    @Override public boolean addCrop1(String phone, int delta) { return addIntField(phone, "crop1", delta); }
    @Override public boolean addCrop2(String phone, int delta) { return addIntField(phone, "crop2", delta); }
    @Override public boolean addCrop3(String phone, int delta) { return addIntField(phone, "crop3", delta); }

    private int safeInt(Integer v) { return v == null ? 0 : v; }
}
